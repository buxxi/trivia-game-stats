package se.omfilm.trivia.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.domain.*;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;
import se.omfilm.trivia.stats.infrastructure.io.FullGame;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static se.omfilm.trivia.stats.domain.GuessOption.*;

@Service
public class PlayerService {
    private final PlayerAliasService playerAliasService;
    private final StatsFilesInfrastructure statsFilesInfrastructure;

    @Autowired
    public PlayerService(PlayerAliasService playerAliasService, StatsFilesInfrastructure statsFilesInfrastructure) {
        this.playerAliasService = playerAliasService;
        this.statsFilesInfrastructure = statsFilesInfrastructure;
    }

    public List<PlayerSummary> getAllSummary() {
        Collection<FullGame> allGames = statsFilesInfrastructure.readAllGames();
        GuessCount totals = getTotals(allGames);
        return getPlayerWithGames(allGames)
                .map(playerCompleteData -> createSummary(playerCompleteData, totals))
                .sorted(Comparator.comparing(PlayerSummary::rating).reversed())
                .toList();
    }

    public Optional<PlayerDetails> getPlayerDetails(String name) {
        Collection<FullGame> allGames = statsFilesInfrastructure.readAllGames();
        return getPlayerWithGames(allGames)
                .filter(data -> data.name().equals(name))
                .findAny()
                .map(this::createDetails);
    }

    public PlayerSummary createSummary(PlayerCompleteData playerCompleteData, GuessCount totals) {
        List<PlayerResult> playerData = playerCompleteData.players().toList();
        List<SingleGuess> guesses = playerCompleteData.guesses().toList();
        return PlayerSummary.of(
                playerCompleteData.name(),
                resolveMainAvatar(playerData),
                resolveSummaryGames(playerData),
                resolveSummaryGuesses(guesses),
                totals
        );
    }

    public PlayerDetails createDetails(PlayerCompleteData playerCompleteData) {
        List<PlayerResult> playerData = playerCompleteData.players().toList();
        List<SingleGuess> guesses = playerCompleteData.guesses().toList();

        return new PlayerDetails(
                playerCompleteData.name(),
                playerAliasService.getAliases(playerCompleteData.name()),
                resolveDetailsAvatars(playerData),
                resolveMinTime(guesses),
                resolveAverageTime(guesses),
                resolveTotalPoints(guesses, Boolean.TRUE),
                resolveTotalPoints(guesses, Boolean.FALSE),
                resolveAverageMultiplier(guesses),
                resolvePlacements(playerData),
                resolveAlternatives(guesses),
                resolveCategories(guesses)
        );
    }

    private List<PlayerDetails.Category> resolveCategories(List<SingleGuess> guesses) {
        GuessCount totals = sumCategory(guesses);
        return guesses.stream()
                .collect(Collectors.groupingBy(SingleGuess::category))
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), sumCategory(e.getValue())))
                .map(e -> PlayerDetails.Category.of(
                        e.getKey(),
                        e.getValue().correct(),
                        e.getValue().incorrect(),
                        e.getValue().unanswered(),
                        e.getValue().totalPointsWon(),
                        e.getValue().totalPointsLost(),
                        totals)
                ).sorted(Comparator.comparing(PlayerDetails.Category::rating).reversed())
                .toList();
    }

    private GuessCount sumCategory(List<SingleGuess> value) {
        return value.stream()
                .map(SingleGuess::toGuessCount)
                .reduce(GuessCount.EMPTY, GuessCount::merge);
    }

    private PlayerDetails.Alternatives resolveAlternatives(List<SingleGuess> guesses) {
        Map<GuessOption, GuessCount> guessMap = guesses.stream()
                .collect(Collectors.toMap(
                        SingleGuess::guessed,
                        SingleGuess::toGuessCount,
                        GuessCount::merge
                ));
        return new PlayerDetails.Alternatives(
                guessMap.get(A).toDetails(),
                guessMap.get(B).toDetails(),
                guessMap.get(C).toDetails(),
                guessMap.get(D).toDetails()
        );
    }

    private List<PlayerDetails.Placement> resolvePlacements(List<PlayerResult> playerData) {
        return playerData.stream()
                .collect(Collectors.groupingBy(PlayerResult::place, Collectors.counting()))
                .entrySet()
                .stream()
                .map(e -> new PlayerDetails.Placement(e.getKey(), e.getValue().intValue(), playerData.size()))
                .toList();

    }

    private BigDecimal resolveAverageMultiplier(List<SingleGuess> guesses) {
        return guesses.stream()
                .map(guess -> new BigDecimal(guess.multiplier()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.DOWN)
                .divide(new BigDecimal(guesses.size()), RoundingMode.DOWN);
    }

    private int resolveTotalPoints(List<SingleGuess> guesses, Boolean correct) {
        return Math.abs(guesses.stream()
                .filter(guess -> correct.equals(guess.correct()))
                .mapToInt(SingleGuess::points)
                .sum());
    }

    private BigDecimal resolveAverageTime(List<SingleGuess> guesses) {
        return guesses.stream()
                .filter(guess -> Boolean.TRUE.equals(guess.correct()))
                .map(SingleGuess::time)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.UP)
                .divide(new BigDecimal(guesses.size()), RoundingMode.UP);
    }

    private BigDecimal resolveMinTime(List<SingleGuess> guesses) {
        return guesses.stream()
                .filter(guess -> Boolean.TRUE.equals(guess.correct()))
                .map(SingleGuess::time)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.UP);
    }

    private List<PlayerDetails.AvatarUsage> resolveDetailsAvatars(List<PlayerResult> playerData) {
        return playerData.stream()
                .collect(Collectors.groupingBy(PlayerResult::avatar, Collectors.counting()))
                .entrySet()
                .stream()
                .map(e -> new PlayerDetails.AvatarUsage(e.getKey(), e.getValue().intValue(), playerData.size()))
                .toList();
    }

    private String resolveMainAvatar(List<PlayerResult> playerData) {
        return resolveDetailsAvatars(playerData).stream()
                .max(Comparator.comparing(PlayerDetails.AvatarUsage::count))
                .map(PlayerDetails.AvatarUsage::name)
                .orElseThrow();
    }

    private PlayerSummary.Games resolveSummaryGames(List<PlayerResult> playerData) {
        return new PlayerSummary.Games(playerData.size(), (int) playerData.stream().filter(PlayerResult::won).count());
    }

    private PlayerSummary.Guesses resolveSummaryGuesses(List<SingleGuess> guesses) {
        GuessCount guessCount = guesses.stream()
                .map(SingleGuess::toGuessCount)
                .reduce(GuessCount.EMPTY, GuessCount::merge);
        return guessCount.toSummary();
    }

    private GuessCount getTotals(Collection<FullGame> allGames) {
        return allGames.stream()
                .map(FullGame::questions)
                .flatMap(Collection::stream)
                .map(FullGame.Question::guesses)
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(guess -> toSingleGuess(guess, null).toGuessCount())
                .reduce(GuessCount.EMPTY, GuessCount::merge);
    }

    private Stream<PlayerCompleteData> getPlayerWithGames(Collection<FullGame> allGames) {
        return allGames.stream()
                .flatMap(game -> game.players().entrySet().stream()
                        .map(entry -> {
                            FullGame.Player player = entry.getValue();
                            String playerId = entry.getKey();
                            return new PlayerCompleteData(
                                    playerAliasService.getMainName(player.name()).orElse(player.name()),
                                    Stream.of(new PlayerResult(player.name(), player.avatar(), player.place())),
                                    game.questions().stream().map(question -> toSingleGuess(question.guesses().get(playerId), question.category()))
                            );
                        })
                ).collect(Collectors.toMap(
                        PlayerCompleteData::name,
                        Function.identity(),
                        (data1, data2) -> new PlayerCompleteData(
                                data1.name(),
                                Stream.concat(data1.players(), data2.players()),
                                Stream.concat(data1.guesses(), data2.guesses())
                        )
                )).values().stream();
    }

    private SingleGuess toSingleGuess(FullGame.Question.Guess guess, String category) {
        return new SingleGuess(GuessOption.valueOf(guess.guessed()), guess.correct(), guess.time(), guess.multiplier(), guess.points(), category);
    }

}

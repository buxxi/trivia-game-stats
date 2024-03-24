package se.omfilm.trivia.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.domain.PlayerDetails;
import se.omfilm.trivia.stats.domain.PlayerSummary;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;
import se.omfilm.trivia.stats.infrastructure.io.FullGame;
import se.omfilm.trivia.stats.util.BayesianEstimate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PlayerService {
    private static final int MINIMUM_GUESS_COUNT = 25;

    private final PlayerAliasService playerAliasService;
    private final StatsFilesInfrastructure statsFilesInfrastructure;

    @Autowired
    public PlayerService(PlayerAliasService playerAliasService, StatsFilesInfrastructure statsFilesInfrastructure) {
        this.playerAliasService = playerAliasService;
        this.statsFilesInfrastructure = statsFilesInfrastructure;
    }

    public List<PlayerSummary> getAllSummary() {
        Collection<FullGame> allGames = statsFilesInfrastructure.readAllGames();
        PlayerSummary.Guesses totals = getTotals(allGames);
        return getPlayerWithGames(allGames)
                .map(PlayerGamesData::createSummary)
                .map(summary -> summary.withRating(BayesianEstimate.calculate(
                        summary.guesses().percentage(),
                        summary.guesses().total(),
                        totals.percentage(),
                        MINIMUM_GUESS_COUNT
                )))
                .sorted(Comparator.comparing(PlayerSummary::rating).reversed())
                .toList();
    }

    public Optional<PlayerDetails> getPlayerDetails(String name) {
        Collection<FullGame> allGames = statsFilesInfrastructure.readAllGames();
        return getPlayerWithGames(allGames)
                .filter(data -> data.name().equals(name))
                .findAny()
                .map(playerGamesData -> playerGamesData.createDetails(playerAliasService));
    }

    private PlayerSummary.Guesses getTotals(Collection<FullGame> allGames) {
        return allGames.stream()
                .map(FullGame::questions)
                .flatMap(Collection::stream)
                .map(FullGame.Question::guesses)
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(guess -> new PlayerGuess(guess, null).toSummary())
                .reduce(new PlayerSummary.Guesses(0, 0, 0),
                        (a, b) -> new PlayerSummary.Guesses(a.correct() + b.correct(), a.incorrect() + b.incorrect(), a.unanswered() + b.unanswered())
                );
    }

    private Stream<PlayerGamesData> getPlayerWithGames(Collection<FullGame> allGames) {
        return allGames.stream()
                .flatMap(game -> game.players().entrySet().stream()
                        .map(entry -> {
                            FullGame.Player player = entry.getValue();
                            String playerId = entry.getKey();
                            return new PlayerGamesData(
                                    playerAliasService.getMainName(player.name()).orElse(player.name()),
                                    Stream.of(player),
                                    game.questions().stream().map(question -> new PlayerGuess(question.guesses().get(playerId), question.category()))
                            );
                        })
                ).collect(Collectors.toMap(
                        PlayerGamesData::name,
                        Function.identity(),
                        (data1, data2) -> new PlayerGamesData(
                                data1.name(),
                                Stream.concat(data1.players(), data2.players()),
                                Stream.concat(data1.guesses(), data2.guesses())
                        )
                )).values().stream();
    }

    private record PlayerGamesData(String name,
                                   Stream<FullGame.Player> players,
                                   Stream<PlayerGuess> guesses) {

        public static final int MINIMUM_CATEGORY_GUESS_COUNT = 5;

        public PlayerSummary createSummary() {
            List<FullGame.Player> playerData = this.players.toList();
            List<PlayerGuess> guesses = this.guesses.toList();
            return new PlayerSummary(name, resolveMainAvatar(playerData), resolveSummaryGames(playerData), BigDecimal.ZERO, resolveSummaryGuesses(guesses));
        }

        public PlayerDetails createDetails(PlayerAliasService playerAliasService) {
            List<FullGame.Player> playerData = this.players.toList();
            List<PlayerGuess> guesses = this.guesses.toList();

            return new PlayerDetails(
                    name(),
                    playerAliasService.getAliases(name()),
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

        private List<PlayerDetails.Category> resolveCategories(List<PlayerGuess> guesses) {
            PlayerDetails.Category totals = sumCategory(guesses);
            return guesses.stream()
                    .collect(Collectors.groupingBy(PlayerGuess::category))
                    .values()
                    .stream()
                    .map(this::sumCategory)
                    .map(category -> category.withRating(BayesianEstimate.calculate(
                            category.percentage(),
                            category.total(),
                            totals.percentage(),
                            MINIMUM_CATEGORY_GUESS_COUNT
                    )))
                    .sorted(Comparator.comparing(PlayerDetails.Category::rating).thenComparing(c -> c.totalPointsWon() - c.totalPointsLost()).reversed())
                    .toList();
        }

        private PlayerDetails.Category sumCategory(List<PlayerGuess> value) {
            return value.stream()
                    .map(PlayerGuess::toCategoryDetails)
                    .reduce(
                            new PlayerDetails.Category(null, 0, 0, 0, 0, 0, BigDecimal.ZERO),
                            (a, b) -> new PlayerDetails.Category(b.name(), a.correct() + b.correct(), a.incorrect() + b.incorrect(), a.unanswered() + b.unanswered(), a.totalPointsWon() + b.totalPointsWon(), a.totalPointsLost() + b.totalPointsLost(), BigDecimal.ZERO)
                    );
        }

        private PlayerDetails.Alternatives resolveAlternatives(List<PlayerGuess> guesses) {
            Map<String, PlayerDetails.Alternatives.Guess> guessMap = guesses.stream()
                    .collect(Collectors.toMap(
                            PlayerGuess::guessed,
                            (PlayerGuess guess) -> new PlayerDetails.Alternatives.Guess(guess.isCorrect() ? 1 : 0, guess.isIncorrect() ? 1 : 0),
                            (a, b) -> new PlayerDetails.Alternatives.Guess(a.correct() + b.correct(), a.incorrect() + b.incorrect())
                    ));
            return new PlayerDetails.Alternatives(guessMap.get("A"), guessMap.get("B"), guessMap.get("C"), guessMap.get("D"));
        }

        private List<PlayerDetails.Placement> resolvePlacements(List<FullGame.Player> playerData) {
            return playerData.stream()
                    .collect(Collectors.toMap(FullGame.Player::place, (player) ->1, Integer::sum))
                    .entrySet()
                    .stream()
                    .map(e -> new PlayerDetails.Placement(e.getKey(), e.getValue(), new BigDecimal(e.getValue()).setScale(2, RoundingMode.UP).divide(new BigDecimal(playerData.size()), RoundingMode.UP).multiply(new BigDecimal(100))))
                    .toList();

        }

        private BigDecimal resolveAverageMultiplier(List<PlayerGuess> guesses) {
            return guesses.stream()
                    .map(guess ->  new BigDecimal(guess.multiplier()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.DOWN)
                    .divide(new BigDecimal(guesses.size()), RoundingMode.DOWN);
        }

        private int resolveTotalPoints(List<PlayerGuess> guesses, Boolean correct) {
            return Math.abs(guesses.stream()
                    .filter(guess -> correct.equals(guess.correct()))
                    .mapToInt(PlayerGuess::points)
                    .sum());
        }

        private BigDecimal resolveAverageTime(List<PlayerGuess> guesses) {
            return guesses.stream()
                    .filter(guess -> Boolean.TRUE.equals(guess.correct()))
                    .map(PlayerGuess::time)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.UP)
                    .divide(new BigDecimal(guesses.size()), RoundingMode.UP);
        }

        private BigDecimal resolveMinTime(List<PlayerGuess> guesses) {
            return guesses.stream()
                    .filter(guess -> Boolean.TRUE.equals(guess.correct()))
                    .map(PlayerGuess::time)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.UP);
        }

        private List<PlayerDetails.AvatarUsage> resolveDetailsAvatars(List<FullGame.Player> playerData) {
            return playerData.stream()
                    .collect(Collectors.toMap(FullGame.Player::avatar, (player) -> 1, Integer::sum))
                    .entrySet()
                    .stream()
                    .map(e -> new PlayerDetails.AvatarUsage(e.getKey(), new BigDecimal(e.getValue()).divide(new BigDecimal(playerData.size()), RoundingMode.UP).multiply(new BigDecimal(100))))
                    .toList();
        }

        private PlayerSummary.Games resolveSummaryGames(List<FullGame.Player> playerData) {
            return new PlayerSummary.Games(playerData.size(), (int) playerData.stream().filter(gameData -> gameData.place() == 1).count());
        }

        private PlayerSummary.Guesses resolveSummaryGuesses(List<PlayerGuess> guesses) {
            int correct = (int) guesses.stream().filter(PlayerGuess::isCorrect).count();
            int incorrect = (int) guesses.stream().filter(PlayerGuess::isIncorrect).count();
            return new PlayerSummary.Guesses(correct, incorrect, guesses.size() - correct - incorrect);
        }

        private String resolveMainAvatar(List<FullGame.Player> playerData) {
            return playerData.stream()
                    .collect(Collectors.groupingBy(
                            FullGame.Player::avatar,
                            Collectors.counting()
                    )).entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElseThrow();
        }
    }

    private record PlayerGuess(
            String guessed,
            Boolean correct,
            BigDecimal time,
            int multiplier,
            int points,
            String category
    ) {
        public PlayerGuess(FullGame.Question.Guess guess, String category) {
            this(guess.guessed(), guess.correct(), guess.time(), guess.multiplier(), guess.points(), category);
        }

        public PlayerSummary.Guesses toSummary() {
            return new PlayerSummary.Guesses(isCorrect() ? 1 : 0, isIncorrect() ? 1 : 0, !isCorrect() && !isIncorrect() ? 1 : 0);
        }

        public PlayerDetails.Category toCategoryDetails() {
            return new PlayerDetails.Category(category(), isCorrect() ? 1 : 0, isIncorrect() ? 1 : 0, !isCorrect() && !isIncorrect() ? 1 : 0, isCorrect() ? points() : 0, isIncorrect() ? Math.abs(points()) : 0, BigDecimal.ZERO);
        }

        private boolean isIncorrect() {
            return Boolean.FALSE.equals(correct());
        }

        private boolean isCorrect() {
            return Boolean.TRUE.equals(correct());
        }
    }
}

package se.omfilm.trivia.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.domain.PlayerSummary;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;
import se.omfilm.trivia.stats.infrastructure.io.FullGame;
import se.omfilm.trivia.stats.util.BayesianEstimate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    private PlayerSummary.Guesses getTotals(Collection<FullGame> allGames) {
        return allGames.stream()
                .map(FullGame::questions)
                .flatMap(Collection::stream)
                .map(FullGame.Question::guesses)
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(guess -> new PlayerSummary.Guesses(isCorrect(guess) ? 1 : 0, isIncorrect(guess) ? 1 : 0, !isCorrect(guess) && !isIncorrect(guess) ? 1 : 0))
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
                                    playerAliasService.getAlias(game.id(), player.name()).orElse(player.name()),
                                    Stream.of(player),
                                    game.questions().stream().map(question -> question.guesses().get(playerId))
                            );
                        })
                ).collect(Collectors.toMap(
                        PlayerGamesData::name,
                        Function.identity(),
                        (data1, data2) -> new PlayerGamesData(
                                data1.name(),
                                Stream.concat(data1.playerData(), data2.playerData()),
                                Stream.concat(data1.guesses(), data2.guesses())
                        )
                )).values().stream();
    }

    private record PlayerGamesData(String name, Stream<FullGame.Player> playerData,
                                   Stream<FullGame.Question.Guess> guesses) {
        public PlayerSummary createSummary() {
            List<FullGame.Player> playerData = this.playerData.toList();
            List<FullGame.Question.Guess> guesses = this.guesses.toList();
            return new PlayerSummary(name, resolveAvatar(playerData), resolveGames(playerData), BigDecimal.ZERO, resolveGuesses(guesses));
        }

        private PlayerSummary.Games resolveGames(List<FullGame.Player> playerData) {
            return new PlayerSummary.Games(playerData.size(), (int) playerData.stream().filter(gameData -> gameData.place() == 1).count());
        }

        private PlayerSummary.Guesses resolveGuesses(List<FullGame.Question.Guess> guesses) {
            int correct = (int) guesses.stream().filter(PlayerService::isCorrect).count();
            int incorrect = (int) guesses.stream().filter(PlayerService::isIncorrect).count();
            return new PlayerSummary.Guesses(correct, incorrect, guesses.size() - correct - incorrect);
        }

        private String resolveAvatar(List<FullGame.Player> playerData) {
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

    private static boolean isIncorrect(FullGame.Question.Guess g) {
        return Boolean.FALSE.equals(g.correct());
    }

    private static boolean isCorrect(FullGame.Question.Guess g) {
        return Boolean.TRUE.equals(g.correct());
    }
}

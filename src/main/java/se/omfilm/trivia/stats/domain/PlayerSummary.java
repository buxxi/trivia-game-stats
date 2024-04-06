package se.omfilm.trivia.stats.domain;

import se.omfilm.trivia.stats.util.BayesianEstimate;

import java.math.BigDecimal;

public record PlayerSummary(
        String name,
        String avatar,
        GamesCount games,
        BigDecimal rating,
        GuessCount guesses
) {
    private static final int MINIMUM_GUESS_COUNT = 25;
    public static final int MINIMUM_WIN_COUNT = 1;

    public static PlayerSummary of(String name, String avatar, GamesCount games, GamesCount gamesTotals, GuessCount guessCount, GuessCount guessTotals) {
        BigDecimal winRating = BayesianEstimate.calculate(games, gamesTotals, MINIMUM_WIN_COUNT).multiply(new BigDecimal("0.75"));
        BigDecimal guessRating = BayesianEstimate.calculate(guessCount.getGuessPercentable(), guessTotals.getGuessPercentable(), MINIMUM_GUESS_COUNT).multiply(new BigDecimal("0.25"));
        BigDecimal averageRating = guessRating.add(winRating);
        return new PlayerSummary(name, avatar, games, averageRating, guessCount);
    }
}

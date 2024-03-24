package se.omfilm.trivia.stats.domain;

import se.omfilm.trivia.stats.util.BayesianEstimate;

import java.math.BigDecimal;

public record PlayerSummary(
        String name,
        String avatar,
        Games games,
        BigDecimal rating,
        Guesses guesses
) {
    private static final int MINIMUM_GUESS_COUNT = 25;

    public record Guesses(
            int correct,
            int incorrect,
            int unanswered
    ) { }

    public record Games(
            int total,
            int wins
    ) {}

    public static PlayerSummary of(String name, String avatar, Games games, Guesses guesses, Percentable totals) {
        BigDecimal rating = BayesianEstimate.calculate(new Percentable() {
            public int count() {
                return guesses.correct();
            }

            public int total() {
                return guesses.correct() + guesses.incorrect() + guesses.unanswered();
            }
        }, totals, MINIMUM_GUESS_COUNT);
        return new PlayerSummary(name, avatar, games, rating, guesses);
    }
}

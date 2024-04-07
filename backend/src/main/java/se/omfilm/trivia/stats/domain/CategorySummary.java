package se.omfilm.trivia.stats.domain;

import se.omfilm.trivia.stats.util.BayesianEstimate;

import java.math.BigDecimal;

public record CategorySummary(
        String name,
        int questions,
        GuessCount guessCount,
        BigDecimal rating
) {
    private static final int MINIMUM_GUESSES = 1;

    public static CategorySummary of(String name, int questions, GuessCount guessCount, GuessCount totals) {
        BigDecimal rating = BayesianEstimate.calculate(guessCount.getGuessPercentable(), totals.getGuessPercentable(), MINIMUM_GUESSES);
        return new CategorySummary(name, questions, guessCount, rating);
    }
}

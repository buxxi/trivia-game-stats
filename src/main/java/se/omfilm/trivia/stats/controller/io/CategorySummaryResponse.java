package se.omfilm.trivia.stats.controller.io;

import se.omfilm.trivia.stats.domain.CategorySummary;

import java.math.BigDecimal;

public record CategorySummaryResponse(
        String name,
        int questions,
        CategoryGuessesResponse guesses,
        BigDecimal rating
) {
    public CategorySummaryResponse(CategorySummary summary) {
        this(summary.name(), summary.questions(), summary.guessCount().toCategorySummary(), summary.rating());
    }

    public record CategoryGuessesResponse(
        int correct,
        int incorrect,
        int unanswered
    ) { }
}

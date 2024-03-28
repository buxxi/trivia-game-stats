package se.omfilm.trivia.stats.domain;

import se.omfilm.trivia.stats.controller.io.CategorySummaryResponse;

public record GuessCount(
        int correct,
        int incorrect,
        int unanswered,
        int totalPointsWon,
        int totalPointsLost
) {
    public static final GuessCount EMPTY = new GuessCount(0, 0, 0, 0, 0);

    public Percentable getGuessPercentable() {
        return new Percentable() {
            public int count() {
                return correct();
            }

            public int total() {
                return correct() + incorrect() + unanswered();
            }
        };
    }

    public Percentable getPointsPercentable() {
        return new Percentable() {
            @Override
            public int count() {
                return totalPointsWon();
            }

            @Override
            public int total() {
                return totalPointsWon() + totalPointsLost();
            }
        };
    }

    public GuessCount merge(GuessCount other) {
        return new GuessCount(
                correct() + other.correct(),
                incorrect() + other.incorrect(),
                unanswered() + other.unanswered(),
                totalPointsWon() + other.totalPointsWon(),
                totalPointsLost() + other.totalPointsLost()
        );
    }

    public PlayerDetails.Alternatives.Guess toDetails() {
        return new PlayerDetails.Alternatives.Guess(correct(), incorrect());
    }

    public PlayerSummary.Guesses toSummary() {
        return new PlayerSummary.Guesses(correct(), incorrect(), unanswered());
    }

    public CategorySummaryResponse.CategoryGuessesResponse toCategorySummary() {
        return new CategorySummaryResponse.CategoryGuessesResponse(correct(), incorrect(), unanswered());
    }
}

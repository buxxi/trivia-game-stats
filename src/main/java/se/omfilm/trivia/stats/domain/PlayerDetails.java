package se.omfilm.trivia.stats.domain;

import se.omfilm.trivia.stats.util.BayesianEstimate;

import java.math.BigDecimal;
import java.util.List;

public record PlayerDetails(
        String name,
        List<String> aliases,
        List<AvatarUsage> avatars,
        BigDecimal fastestTime,
        BigDecimal averageTime,
        int totalPointsWon,
        int totalPointsLost,
        BigDecimal averageMultiplier,
        List<Placement> placements,
        Alternatives alternatives,
        List<Category> categories
) {
    public record AvatarUsage(
            String name,
            int count,
            int total
    ) implements Percentable {
    }

    public record Placement(
            int place,
            int count,
            int total
    ) implements Percentable {}

    public record Alternatives(
            Guess a,
            Guess b,
            Guess c,
            Guess d
    ) {
        public record Guess(
                int correct,
                int incorrect
        ) implements Percentable {
            public int count() {
                return correct;
            }

            public int total() {
                return correct + incorrect;
            }
        }
    }

    public record Category(
            String name,
            int correct,
            int incorrect,
            int unanswered,
            int totalPointsWon,
            int totalPointsLost,
            BigDecimal rating
    ) {
        private static final int MINIMUM_CATEGORY_GUESS_COUNT = 5;

        public static Category of(String name, int correct, int incorrect, int unanswered, int totalPointsWon, int totalPointsLost, Percentable totals) {
            //TODO: maybe have totalPoints won/lost as part of the rating?
            BigDecimal rating = BayesianEstimate.calculate(new Percentable() {
                public int count() {
                    return correct;
                }

                public int total() {
                    return correct + incorrect;
                }
            }, totals, MINIMUM_CATEGORY_GUESS_COUNT);
            return new Category(name, correct, incorrect, unanswered, totalPointsWon, totalPointsLost, rating);
        }
    }
}

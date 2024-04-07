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
        GuessCount totals,
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
    ) implements Percentable {
        public boolean won() {
            return place == 1;
        }
    }

    public record Alternatives(
            GuessCount a,
            GuessCount b,
            GuessCount c,
            GuessCount d
    ) { }

    public record Category(
            String name,
            GuessCount count,
            BigDecimal rating
    ) {
        private static final int MINIMUM_CATEGORY_POINTS_COUNT = 1000;
        private static final int MINIMUM_CATEGORY_GUESS_COUNT = 5;

        public static Category of(String name, GuessCount guessCount, GuessCount totals) {
            BigDecimal guessRating = BayesianEstimate.calculate(guessCount.getGuessPercentable(), totals.getGuessPercentable(), MINIMUM_CATEGORY_GUESS_COUNT).multiply(new BigDecimal("0.75"));
            BigDecimal pointsRating = BayesianEstimate.calculate(guessCount.getPointsPercentable(), totals.getPointsPercentable(), MINIMUM_CATEGORY_POINTS_COUNT).multiply(new BigDecimal("0.25"));
            BigDecimal rating = pointsRating.add(guessRating);
            return new Category(name, guessCount, rating);
        }
    }
}

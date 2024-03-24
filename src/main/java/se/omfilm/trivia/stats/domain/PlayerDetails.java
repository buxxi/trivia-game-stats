package se.omfilm.trivia.stats.domain;

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
            BigDecimal percentage
    ) {
    }

    public record Placement(
            int place,
            int total,
            BigDecimal percentage
    ) {}

    public record Alternatives(
            Guess a,
            Guess b,
            Guess c,
            Guess d
    ) {
        public record Guess(
                int correct,
                int incorrect
        ) {
            public long total() {
                return correct + incorrect;
            }

            public float percentage() {
                return (correct / (float) total()) * 100;
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
        public Category withRating(BigDecimal rating) {
            return new Category(this.name(), this.correct(), this.incorrect(), this.unanswered(), this.totalPointsWon(), this.totalPointsLost(), rating);
        }

        public long total() {
            return correct + incorrect;
        }

        public float percentage() {
            return (correct / (float) total()) * 100;
        }
    }
}

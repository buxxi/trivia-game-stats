package se.omfilm.trivia.stats.domain;

import java.math.BigDecimal;

public record PlayerSummary(
        String name,
        String avatar,
        Games games,
        BigDecimal rating,
        Guesses guesses
) {
    public record Guesses(
            int correct,
            int incorrect,
            int unanswered
    ) {
        public long total() {
            return correct + incorrect + unanswered;
        }

        public float percentage() {
            return (correct / (float) total()) * 100;
        }
    }

    public record Games(
            int total,
            int wins
    ) {}

    public PlayerSummary withRating(BigDecimal rating) {
        return new PlayerSummary(name, avatar, games, rating, guesses);
    }
}

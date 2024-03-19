package se.omfilm.trivia.stats.controller.io;

import java.math.BigDecimal;
import java.util.List;

public record PlayerDetailsResponse(
        String name,
        List<String> aliases,
        List<AvatarResponse> avatars,
        BigDecimal fastestTime,
        BigDecimal averageTime,
        int totalWon,
        int totalLost,
        List<PlacementResponse> placements,
        AlternativesResponse guesses,
        List<CategoryResponse> categories
) {
    public record AvatarResponse(
            String value,
            BigDecimal percentage
    ) {
    }

    public record PlacementResponse(
            int place,
            BigDecimal percentage
    ) {
    }

    public record AlternativesResponse(
            GuessResponse a,
            GuessResponse b,
            GuessResponse c,
            GuessResponse d
    ) {
        public record GuessResponse(
                int correct,
                int incorrect
        ) {
        }
    }

    public record CategoryResponse(
            String name,
            int correct,
            int incorrect,
            int unanswered,
            int totalWon,
            int totalLost,
            BigDecimal rating
    ) {
    }
}

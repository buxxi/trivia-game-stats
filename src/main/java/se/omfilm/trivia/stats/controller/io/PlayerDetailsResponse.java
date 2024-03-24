package se.omfilm.trivia.stats.controller.io;

import se.omfilm.trivia.stats.domain.PlayerDetails;

import java.math.BigDecimal;
import java.util.List;

public record PlayerDetailsResponse(
        String name,
        List<String> aliases,
        List<AvatarResponse> avatars,
        BigDecimal fastestTime,
        BigDecimal averageTime,
        int totalPointsWon,
        int totalPointsLost,
        BigDecimal averageMultiplier,
        List<PlacementResponse> placements,
        AlternativesResponse guesses,
        List<CategoryResponse> categories
) {
    public record AvatarResponse(
            String name,
            BigDecimal percentage
    ) {
        public AvatarResponse(PlayerDetails.AvatarUsage response) {
            this(response.name(), response.percentage());
        }
    }

    public record PlacementResponse(
            int place,
            int total,
            BigDecimal percentage
    ) {
        public PlacementResponse(PlayerDetails.Placement placement) {
            this(placement.place(), placement.count(), placement.percentage());
        }
    }

    public record AlternativesResponse(
            GuessResponse a,
            GuessResponse b,
            GuessResponse c,
            GuessResponse d
    ) {
        public AlternativesResponse(PlayerDetails.Alternatives alternatives) {
            this(new GuessResponse(alternatives.a()), new GuessResponse(alternatives.b()), new GuessResponse(alternatives.c()), new GuessResponse(alternatives.d()));
        }

        public record GuessResponse(
                int correct,
                int incorrect,
                BigDecimal percentage
        ) {
            public GuessResponse(PlayerDetails.Alternatives.Guess guess) {
                this(guess.correct(), guess.incorrect(), guess.percentage());
            }
        }
    }

    public record CategoryResponse(
            String name,
            int correct,
            int incorrect,
            int unanswered,
            int totalPointsWon,
            int totalPointsLost,
            BigDecimal rating
    ) {
        public CategoryResponse(PlayerDetails.Category category) {
            this(category.name(), category.correct(), category.incorrect(), category.unanswered(), category.totalPointsWon(), category.totalPointsLost(), category.rating());
        }
    }

    public PlayerDetailsResponse(PlayerDetails details) {
        this(
                details.name(),
                details.aliases(),
                details.avatars().stream().map(AvatarResponse::new).toList(),
                details.fastestTime(),
                details.averageTime(),
                details.totalPointsWon(),
                details.totalPointsLost(),
                details.averageMultiplier(),
                details.placements().stream().map(PlacementResponse::new).toList(),
                new AlternativesResponse(details.alternatives()),
                details.categories().stream().map(CategoryResponse::new).toList());
    }
}

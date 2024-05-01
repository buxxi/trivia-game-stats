package se.omfilm.trivia.stats.controller.io;

import se.omfilm.trivia.stats.domain.GameSummary;
import se.omfilm.trivia.stats.domain.PlayerResult;

import java.time.ZonedDateTime;

public record GameSummaryResponse(
        String id,
        int players,
        int questions,
        GameSummaryCategoriesResponse categories,
        ZonedDateTime started,
        int durationMinutes,
        GameSummaryWinnerResponse winner
) {
    public GameSummaryResponse(GameSummary summary) {
        this(summary.id(), summary.players(), summary.questions(), new GameSummaryCategoriesResponse(summary.categories()), summary.started(), (int) summary.duration().toMinutes(), new GameSummaryWinnerResponse(summary.winner()));
    }

    public record GameSummaryWinnerResponse(
            String name,
            String avatar,
            int points
    ) {
        public GameSummaryWinnerResponse(PlayerResult winner) {
            this(winner.name(), winner.avatar(), winner.points());
        }
    }

    public record GameSummaryCategoriesResponse(
            int used,
            int selected
    ) {

        public GameSummaryCategoriesResponse(GameSummary.GameSummaryCategories categories) {
            this(categories.used(), categories.selected());
        }
    }
}

package se.omfilm.trivia.stats.domain;

import java.time.Duration;
import java.time.ZonedDateTime;

public record GameSummary(
        String id,
        int players,
        int questions,
        GameSummaryCategories categories,
        ZonedDateTime started,
        Duration duration,
        PlayerResult winner
) {
    public record GameSummaryCategories(
            int used,
            int selected
    ) {}

    public static GameSummary from(GameDetails details) {
        int categoryCount = (int) details.question().stream().map(GameDetails.GameQuestionDetails::category).distinct().count();
        return new GameSummary(
                details.id(),
                details.players().size(),
                details.question().size(),
                new GameSummaryCategories(categoryCount, categoryCount), //TODO: How to get the proper names for selected? That should be in the details
                details.started(),
                details.duration(),
                details.players().stream().filter(PlayerResult::won).findAny().orElseThrow()
        );
    }
}

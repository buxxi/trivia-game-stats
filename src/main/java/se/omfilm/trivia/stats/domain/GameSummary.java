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
}

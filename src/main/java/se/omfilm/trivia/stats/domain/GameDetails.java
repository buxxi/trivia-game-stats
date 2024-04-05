package se.omfilm.trivia.stats.domain;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

public record GameDetails(
        ZonedDateTime started,
        Duration duration,
        List<GamePlayerDetails> players,
        List<GameQuestionDetails> question
) {
    public record GamePlayerDetails(
            String name,
            String avatar,
            int points,
            int place
    ) {}

    public record GameQuestionDetails(
        String category,
        String question,
        List<GameAnswerDetails> answers
    ) {}

    public record GameAnswerDetails(
        String answer,
        boolean correct,
        List<String> playersGuessed
    ) {}
}

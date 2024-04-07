package se.omfilm.trivia.stats.domain;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

public record GameDetails(
        String id,
        ZonedDateTime started,
        Duration duration,
        List<PlayerResult> players,
        List<GameQuestionDetails> question
) {
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

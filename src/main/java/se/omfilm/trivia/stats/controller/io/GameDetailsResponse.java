package se.omfilm.trivia.stats.controller.io;

import se.omfilm.trivia.stats.domain.GameDetails;

import java.time.ZonedDateTime;
import java.util.List;

public record GameDetailsResponse(
        ZonedDateTime started,
        int durationMinutes,
        List<GamePlayerDetailsResponse> players,
        List<GameQuestionDetailsResponse> questions
) {
    public GameDetailsResponse(GameDetails gameDetails) {
        this(
                gameDetails.started(),
                gameDetails.duration().toMinutesPart(),
                gameDetails.players().stream().map(GamePlayerDetailsResponse::new).toList(),
                gameDetails.question().stream().map(GameQuestionDetailsResponse::new).toList()
        );
    }

    public record GamePlayerDetailsResponse(
            String name,
            String avatar,
            int points,
            int place
    ) {
       public GamePlayerDetailsResponse(GameDetails.GamePlayerDetails player) {
           this(player.name(), player.avatar(), player.points(), player.place());
       }
    }

    public record GameQuestionDetailsResponse(
            String category,
            String question,
            List<GameAnswerDetailsResponse> answers
    ) {
        public GameQuestionDetailsResponse(GameDetails.GameQuestionDetails question) {
            this(question.category(), question.question(), question.answers().stream().map(GameAnswerDetailsResponse::new).toList());
        }
    }

    public record GameAnswerDetailsResponse(
            String answer,
            boolean correct,
            List<String> playersGuessed
    ) {
        public GameAnswerDetailsResponse(GameDetails.GameAnswerDetails answer) {
            this(answer.answer(), answer.correct(), answer.playersGuessed());
        }
    }
}

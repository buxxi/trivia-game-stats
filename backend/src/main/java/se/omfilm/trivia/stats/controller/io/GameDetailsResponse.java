package se.omfilm.trivia.stats.controller.io;

import se.omfilm.trivia.stats.domain.GameDetails;
import se.omfilm.trivia.stats.domain.PlayerResult;

import java.time.ZonedDateTime;
import java.util.List;

public record GameDetailsResponse(
        ZonedDateTime started,
        int durationMinutes,
        List<GamePlayerDetailsResponse> players,
        List<GameQuestionDetailsResponse> questions
) {
    public GameDetailsResponse(GameDetails gameDetails, String avatarPath) {
        this(
                gameDetails.started(),
                (int) gameDetails.duration().toMinutes(),
                gameDetails.players().stream().map(e -> new GamePlayerDetailsResponse(e, avatarPath)).toList(),
                gameDetails.question().stream().map(GameQuestionDetailsResponse::new).toList()
        );
    }

    public record GamePlayerDetailsResponse(
            String name,
            GameAvatarDetailsResponse avatar,
            int points,
            int place
    ) {
       public GamePlayerDetailsResponse(PlayerResult player, String avatarPath) {
           this(player.name(), new GameAvatarDetailsResponse(player.avatar(), String.format(avatarPath, player.avatar())), player.points(), player.place());
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

    public record GameAvatarDetailsResponse(
            String name,
            String url
    ) {}
}

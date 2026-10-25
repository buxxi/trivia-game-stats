package se.omfilm.trivia.stats.controller.io;

import se.omfilm.trivia.stats.domain.GamesCount;
import se.omfilm.trivia.stats.domain.GuessCount;
import se.omfilm.trivia.stats.domain.PlayerSummary;

import java.math.BigDecimal;

public record PlayerSummaryResponse(
        String name,
        String avatar,
        GamesResponse games,
        BigDecimal rating,
        GuessesResponse guesses
) {
    public record GuessesResponse(
            int correct,
            int incorrect,
            int unanswered
    ) {
        GuessesResponse(GuessCount guesses) {
            this(guesses.correct(), guesses.incorrect(), guesses.unanswered());
        }
    }

    public record GamesResponse(
            int total,
            int wins
    ) {
        GamesResponse(GamesCount games) {
            this(games.total(), games.wins());
        }
    }

    public PlayerSummaryResponse(PlayerSummary summary) {
        this(summary.name(), summary.avatar(), new GamesResponse(summary.games()), summary.rating(), new GuessesResponse(summary.guesses()));
    }
}

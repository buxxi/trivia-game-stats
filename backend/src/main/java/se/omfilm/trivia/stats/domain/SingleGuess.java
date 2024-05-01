package se.omfilm.trivia.stats.domain;

import java.math.BigDecimal;

public record SingleGuess(
        GuessOption guessed,
        Boolean correct,
        BigDecimal time,
        int multiplier,
        int points,
        String category
) {
    public GuessCount toGuessCount() {
        if (guessed() == GuessOption.UNANSWERED) {
            return GuessCount.SINGLE_UNANSWERED;
        }
        return new GuessCount(toCorrectCount(), toIncorrectCount(), 0, toPointsWon(), toPointsLost());
    }

    private int toCorrectCount() {
        return Boolean.TRUE.equals(this.correct()) ? 1 : 0;
    }

    private int toIncorrectCount() {
        return Boolean.FALSE.equals(this.correct()) ? 1 : 0;
    }

    private int toPointsWon() {
        return Boolean.TRUE.equals(this.correct()) ? points() : 0;
    }

    private int toPointsLost() {
        return Boolean.FALSE.equals(this.correct()) ? Math.abs(points()) : 0;
    }
}

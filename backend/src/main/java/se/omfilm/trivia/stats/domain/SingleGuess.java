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
        return new GuessCount(toCorrectCount(), toIncorrectCount(), toUnansweredCount(), toPointsWon(), toPointsLost());
    }

    private int toCorrectCount() {
        return Boolean.TRUE.equals(this.correct()) ? 1 : 0;
    }

    private int toIncorrectCount() {
        return Boolean.FALSE.equals(this.correct()) ? 1 : 0;
    }

    private int toUnansweredCount() {
        return this.correct() == null ? 1 : 0;
    }

    private int toPointsWon() {
        return Boolean.TRUE.equals(this.correct()) ? points() : 0;
    }

    private int toPointsLost() {
        return Boolean.FALSE.equals(this.correct()) ? Math.abs(points()) : 0;
    }
}

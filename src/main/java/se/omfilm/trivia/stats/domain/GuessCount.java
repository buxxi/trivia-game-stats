package se.omfilm.trivia.stats.domain;

public record GuessCount(
        int correct,
        int incorrect,
        int unanswered,
        int totalPointsWon,
        int totalPointsLost
) implements Percentable {
    public static final GuessCount EMPTY = new GuessCount(0, 0, 0, 0, 0);

    public int count() {
        return correct();
    }

    public int total() {
        return correct() + incorrect() + unanswered();
    }

    public GuessCount merge(GuessCount other) {
        return new GuessCount(
                correct() + other.correct(),
                incorrect() + other.incorrect(),
                unanswered() + other.unanswered(),
                totalPointsWon() + other.totalPointsWon(),
                totalPointsLost() + other.totalPointsLost()
        );
    }

    public PlayerDetails.Alternatives.Guess toDetails() {
        return new PlayerDetails.Alternatives.Guess(correct(), incorrect());
    }

    public PlayerSummary.Guesses toSummary() {
        return new PlayerSummary.Guesses(correct(), incorrect(), unanswered());
    }
}

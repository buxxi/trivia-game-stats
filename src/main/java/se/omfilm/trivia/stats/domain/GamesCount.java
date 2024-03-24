package se.omfilm.trivia.stats.domain;

public record GamesCount(
        int wins,
        int losses
) implements Percentable {
    @Override
    public int count() {
        return wins();
    }

    @Override
    public int total() {
        return wins() + losses();
    }

    public PlayerSummary.Games toGamesSummary() {
        return new PlayerSummary.Games(total(), wins());
    }
}

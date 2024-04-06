package se.omfilm.trivia.stats.domain;

public record PlayerResult (
        String name,
        String avatar,
        int place,
        int points
){
    public boolean won() {
        return place() == 1;
    }
}

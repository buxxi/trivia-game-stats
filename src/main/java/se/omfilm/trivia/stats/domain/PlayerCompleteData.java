package se.omfilm.trivia.stats.domain;

import java.util.stream.Stream;

public record PlayerCompleteData(
        String name,
        Stream<PlayerResult> players,
        Stream<SingleGuess> guesses) {
}

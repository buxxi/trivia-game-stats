package se.omfilm.trivia.stats.controller.io;

public record StatisticsResponse (
    int players,
    int questions,
    int categories,
    int durationMinutes
    ) {}

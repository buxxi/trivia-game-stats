package se.omfilm.trivia.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import se.omfilm.trivia.stats.controller.io.GameDetailsResponse;
import se.omfilm.trivia.stats.controller.io.GameSummaryResponse;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;

import java.util.List;

@RestController
public class GamesController {
    private final StatsFilesInfrastructure statsFilesInfrastructure;

    @Autowired
    public GamesController(StatsFilesInfrastructure statsFilesInfrastructure) {
        this.statsFilesInfrastructure = statsFilesInfrastructure;
    }

    @GetMapping(value = "/games", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GameSummaryResponse> getAllGames() {
        throw new UnsupportedOperationException("Not implemented"); //TODO
    }

    @GetMapping(value = "/games/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GameDetailsResponse getGameDetails(@PathVariable("id") String id) {
        throw new UnsupportedOperationException("Not implemented"); //TODO
    }
}

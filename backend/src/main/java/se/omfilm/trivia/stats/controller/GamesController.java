package se.omfilm.trivia.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import se.omfilm.trivia.stats.controller.io.GameDetailsResponse;
import se.omfilm.trivia.stats.controller.io.GameSummaryResponse;
import se.omfilm.trivia.stats.service.GamesService;

import java.util.List;

@RestController
public class GamesController {
    private final GamesService gamesService;

    @Autowired
    public GamesController(GamesService gamesService) {
        this.gamesService = gamesService;
    }

    @GetMapping(value = "/games", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GameSummaryResponse> getAllGames() {
        return gamesService.getAllSummary().stream().map(GameSummaryResponse::new).toList();
    }

    @GetMapping(value = "/games/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GameDetailsResponse getGameDetails(@PathVariable("id") String id) {
        return gamesService.getGameDetails(id)
                .map(GameDetailsResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with id: " + id));
    }
}

package se.omfilm.trivia.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import se.omfilm.trivia.stats.controller.io.GameDetailsResponse;
import se.omfilm.trivia.stats.controller.io.GameSummaryResponse;
import se.omfilm.trivia.stats.service.GamesService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
public class GamesController {
    private final GamesService gamesService;
    private final String avatarUrlPattern;

    @Autowired
    public GamesController(GamesService gamesService, @Value("${avatar.url.pattern}") String avatarUrlPattern) {
        this.gamesService = gamesService;
        this.avatarUrlPattern = avatarUrlPattern;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GameSummaryResponse> getAllGames() {
        return gamesService.getAllSummary().stream().map(GameSummaryResponse::new).toList();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GameDetailsResponse getGameDetails(@PathVariable("id") String id) {
        return gamesService.getGameDetails(id)
                .map(gameDetails -> new GameDetailsResponse(gameDetails, avatarUrlPattern))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No game with id: " + id));
    }
}

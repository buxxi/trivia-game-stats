package se.omfilm.trivia.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import se.omfilm.trivia.stats.controller.io.PlayerDetailsResponse;
import se.omfilm.trivia.stats.controller.io.PlayerSummaryResponse;
import se.omfilm.trivia.stats.service.PlayerService;

import java.util.List;

@RestController
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = "/players", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerSummaryResponse> getAllPlayers() {
        return playerService.getAllSummary().stream().map(PlayerSummaryResponse::new).toList();
    }

    @GetMapping(value = "/players/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PlayerDetailsResponse getPlayerDetails(@PathVariable("name") String name) {
        return playerService.getPlayerDetails(name)
                .map(PlayerDetailsResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with name: " + name));
    }
}

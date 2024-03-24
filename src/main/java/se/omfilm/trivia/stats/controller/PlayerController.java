package se.omfilm.trivia.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.omfilm.trivia.stats.controller.io.PlayerDetailsResponse;
import se.omfilm.trivia.stats.controller.io.PlayerSummaryResponse;
import se.omfilm.trivia.stats.service.PlayerAliasService;
import se.omfilm.trivia.stats.service.PlayerService;

import java.util.List;

@RestController
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerAliasService playerAliasService;

    @Autowired
    public PlayerController(PlayerService playerService, PlayerAliasService playerAliasService) {
        this.playerService = playerService;
        this.playerAliasService = playerAliasService;
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

    @PutMapping(value = "/players/{name}/alias")
    public void addAlias(@PathVariable("name") String name, @RequestBody List<String> aliases) {
        playerAliasService.addAliases(name, aliases);
    }

    @DeleteMapping(value = "/players/{name}/alias")
    public void deleteAlias(@PathVariable("name") String name, @RequestBody List<String> aliases) {
        playerAliasService.removeAliases(name, aliases);
    }
}

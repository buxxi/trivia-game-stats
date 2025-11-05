package se.omfilm.trivia.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/api/v1/players")
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerAliasService playerAliasService;
    private final String avatarUrlPattern;

    @Autowired
    public PlayerController(PlayerService playerService, PlayerAliasService playerAliasService, @Value("${avatar.url.pattern}") String avatarUrlPattern) {
        this.playerService = playerService;
        this.playerAliasService = playerAliasService;
        this.avatarUrlPattern = avatarUrlPattern;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlayerSummaryResponse> getAllPlayers() {
        return playerService.getAllSummary().stream().map(summary -> new PlayerSummaryResponse(summary, avatarUrlPattern)).toList();
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PlayerDetailsResponse getPlayerDetails(@PathVariable("name") String name) {
        return playerService.getPlayerDetails(name)
                .map(PlayerDetailsResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No player with name: " + name));
    }

    @PutMapping(value = "/{name}/alias")
    public void setAlias(@PathVariable("name") String name, @RequestBody List<String> aliases) {
        playerAliasService.setAliases(name, aliases);
    }
}

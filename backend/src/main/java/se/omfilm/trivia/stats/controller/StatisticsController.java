package se.omfilm.trivia.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.omfilm.trivia.stats.controller.io.StatisticsResponse;
import se.omfilm.trivia.stats.domain.CategorySummary;
import se.omfilm.trivia.stats.domain.GameSummary;
import se.omfilm.trivia.stats.domain.PlayerSummary;
import se.omfilm.trivia.stats.service.CategoriesService;
import se.omfilm.trivia.stats.service.GamesService;
import se.omfilm.trivia.stats.service.PlayerService;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
    private final GamesService gamesService;
    private final PlayerService playerService;
    private final CategoriesService categoriesService;

    @Autowired
    public StatisticsController(GamesService gamesService, PlayerService playerService, CategoriesService categoriesService) {
        this.gamesService = gamesService;
        this.playerService = playerService;
        this.categoriesService = categoriesService;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatisticsResponse getTotals() {
        List<GameSummary> games = gamesService.getAllSummary();
        List<PlayerSummary> players = playerService.getAllSummary();
        List<CategorySummary> categories = categoriesService.getAllCategories();

        int questions = games.stream().mapToInt(GameSummary::questions).sum();
        Duration totalDuration = games.stream().map(GameSummary::duration).reduce(Duration.ZERO, Duration::plus);

        return new StatisticsResponse(
                players.size(),
                questions,
                categories.size(),
                (int) totalDuration.toMinutes()
        );
    }
}

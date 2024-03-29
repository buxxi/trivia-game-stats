package se.omfilm.trivia.stats.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.domain.GameSummary;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;
import se.omfilm.trivia.stats.infrastructure.io.FullGame;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class GamesService {
    private final StatsFilesInfrastructure statsFilesInfrastructure;
    private final PlayerAliasService playerAliasService;

    @Autowired
    public GamesService(StatsFilesInfrastructure statsFilesInfrastructure, PlayerAliasService playerAliasService) {
        this.statsFilesInfrastructure = statsFilesInfrastructure;
        this.playerAliasService = playerAliasService;
    }

    public List<GameSummary> getAllSummary() {
        return statsFilesInfrastructure.readAllGames().stream()
                .map(this::toSummary)
                .sorted(Comparator.comparing(GameSummary::started).reversed())
                .toList();
    }

    private GameSummary toSummary(FullGame fullGame) {
        int players = fullGame.players().size();
        int questions = fullGame.questions().size();
        int categoriesSelected = (int) fullGame.config().categories().values().stream().filter(b -> b.equals(Boolean.TRUE)).count();
        int categoriesUsed = (int) fullGame.questions().stream().map(FullGame.Question::category).distinct().count();
        ZonedDateTime started = fullGame.started().truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.of("Europe/Stockholm"));
        Duration duration = Duration.between(fullGame.started(), fullGame.ended());
        GameSummary.GameSummaryCategories categories = new GameSummary.GameSummaryCategories(categoriesUsed, categoriesSelected);
        GameSummary.GameSummaryWinner winner = fullGame.players().values().stream()
                .filter(p -> p.place() == 1)
                .findFirst()
                .map(p -> new GameSummary.GameSummaryWinner(playerAliasService.getMainName(p.name()).orElse(p.name()), p.avatar(), p.points()))
                .orElseThrow(); //TODO: what do to, skip entire game if no players?
        return new GameSummary(
                fullGame.uuid(),
                players,
                questions,
                categories,
                started,
                duration,
                winner
        );
    }
}

package se.omfilm.trivia.stats.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.domain.GameDetails;
import se.omfilm.trivia.stats.domain.GameSummary;
import se.omfilm.trivia.stats.domain.GuessOption;
import se.omfilm.trivia.stats.domain.PlayerResult;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;
import se.omfilm.trivia.stats.infrastructure.io.FullGame;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<GameDetails> getGameDetails(String id) {
        return statsFilesInfrastructure.readAllGames().stream()
                .filter(g -> g.uuid().equals(id))
                .findAny()
                .map(this::toDetails);
    }

    private GameDetails toDetails(FullGame fullGame) {
        Map<String, String> playerIdToName = fullGame.players().entrySet().stream()
                .map(e -> Map.entry(e.getKey(), playerAliasService.getMainName(e.getValue().name()).orElse(e.getValue().name())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        ZonedDateTime started = fullGame.started().truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.of("Europe/Stockholm"));
        Duration duration = Duration.between(fullGame.started(), fullGame.ended());
        List<PlayerResult> players = fullGame.players().values().stream()
                .map(this::toPlayerResult)
                .sorted(Comparator.comparing(PlayerResult::place))
                .toList();
        List<GameDetails.GameQuestionDetails> questions = fullGame.questions().stream()
                .map(question -> toDetails(question, playerIdToName))
                .toList();
        return new GameDetails(started, duration, players, questions);
    }

    private PlayerResult toPlayerResult(FullGame.Player player) {
        return new PlayerResult(player.name(), player.avatar(), player.place(), player.points());
    }

    private GameDetails.GameQuestionDetails toDetails(FullGame.Question question, Map<String, String> playerIdToName) {
        return new GameDetails.GameQuestionDetails(
                question.category(),
                question.question(),
                question.answers().entrySet().stream()
                        .map(e -> {
                            GuessOption option = GuessOption.valueOf(e.getKey());
                            List<String> playersGuessed = toPlayersGuessed(question, playerIdToName, option);
                            return new GameDetails.GameAnswerDetails(e.getValue(), e.getValue().equals(question.correct()), playersGuessed);
                        })
                        .toList()
        );
    }

    private List<String> toPlayersGuessed(FullGame.Question question, Map<String, String> playerIdToName, GuessOption option) {
        return question.guesses().entrySet().stream()
                .filter(guess -> GuessOption.valueOf(guess.getValue().guessed()) == option)
                .map(guess -> playerIdToName.get(guess.getKey()))
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
        PlayerResult winner = fullGame.players().values().stream()
                .filter(p -> p.place() == 1)
                .findFirst()
                .map(p -> new PlayerResult(playerAliasService.getMainName(p.name()).orElse(p.name()), p.avatar(), p.place(), p.points()))
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

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
                .map(this::toDetails)
                .map(GameSummary::from)
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
        return new GameDetails(fullGame.uuid(), started, duration, players, questions);
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
                .filter(guess -> GuessOption.from(guess.getValue().guessed()) == option)
                .map(guess -> playerIdToName.get(guess.getKey()))
                .toList();
    }
}

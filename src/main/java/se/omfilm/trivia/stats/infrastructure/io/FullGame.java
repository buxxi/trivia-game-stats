package se.omfilm.trivia.stats.infrastructure.io;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record FullGame(
        @JsonProperty("id")
        String id,
        @JsonProperty("uuid")
        String uuid,
        @JsonProperty("config")
        Config config,
        @JsonProperty("started")
        Instant started,
        @JsonProperty("ended")
        Instant ended,
        @JsonProperty("questions")
        List<Question> questions,
        @JsonProperty("players")
        Map<String, Player> players
) {

    public record Config(
            @JsonProperty("questions")
            int questions,
            @JsonProperty("time")
            int time,
            @JsonProperty("pointsPerRound")
            int pointsPerRound,
            @JsonProperty("stopOnAnswers")
            boolean stopOnAnswers,
            @JsonProperty("allowMultiplier")
            boolean allowMultiplier,
            @JsonProperty("maxMultiplier")
            int maxMultiplier,
            @JsonProperty("saveStatistics")
            boolean saveStatistics,
            @JsonProperty("sound")
            Sound sound,
            @JsonProperty("categories")
            Map<String, Boolean> categories,
            @JsonProperty("fullscreen")
            boolean fullscreen,
            @JsonProperty("categorySpinner")
            boolean categorySpinner
    ) {
        public record Sound(
                @JsonProperty("backgroundMusic")
                boolean backgroundMusic,
                @JsonProperty("soundEffects")
                boolean soundEffects,
                @JsonProperty("text2Speech")
                boolean text2Speech
        ) {
        }
    }

    public record Player(
            @JsonProperty("name")
            String name,
            @JsonProperty("avatar")
            String avatar,
            @JsonProperty("points")
            int points,
            @JsonProperty("place")
            int place
    ) {
    }

    public record Question(
            @JsonProperty("category")
            String category,
            @JsonProperty("question")
            String question,
            @JsonProperty("answers")
            Map<String, String> answers,
            @JsonProperty("correct")
            String correct,
            @JsonProperty("guesses")
            Map<String, Guess> guesses
    ) {
        public record Guess(
                @JsonProperty("guessed")
                @Nullable
                String guessed,
                @Nullable
                @JsonProperty("correct")
                Boolean correct,
                @JsonProperty("time")
                @Nullable
                BigDecimal time,
                @JsonProperty("multiplier")
                int multiplier,
                @JsonProperty("points")
                int points
        ) {
        }
    }
}

package se.omfilm.trivia.stats.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Optional;

@Configuration
public class DataSettings {
    @Bean
    @Qualifier("data.path")
    public Path resolveDataPath() {
        return Optional.ofNullable(System.getenv("XDG_DATA_HOME"))
                .map(Path::of)
                .orElse(Path.of(System.getenv("HOME")).resolve(Path.of(".local/share")))
                .resolve("trivia-game/stats");
    }
}

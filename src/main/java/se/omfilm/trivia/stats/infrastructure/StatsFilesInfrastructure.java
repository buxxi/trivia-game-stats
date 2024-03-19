package se.omfilm.trivia.stats.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.infrastructure.io.FullGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@Service
public class StatsFilesInfrastructure {
    private final Path rootPath;
    private final ObjectMapper objectMapper;

    @Autowired
    public StatsFilesInfrastructure(@Value("${stats.file.path}") Path rootPath, ObjectMapper objectMapper) {
        this.rootPath = rootPath;
        this.objectMapper = objectMapper;
    }

    @Cacheable("fullgames")
    public Collection<FullGame> readAllGames() {
        try (var files = Files.list(rootPath)) {
            return files
                    .parallel()
                    .filter(p -> p.toString().endsWith(".json"))
                    .map(this::toFullGame)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FullGame toFullGame(Path path) {
        try {
            return objectMapper.readValue(Files.newInputStream(path), FullGame.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

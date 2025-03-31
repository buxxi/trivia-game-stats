package se.omfilm.trivia.stats.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.infrastructure.io.FullGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@Service
public class StatsFilesInfrastructure {
    public static final String CACHE_KEY = "readAllGames";
    private final Path rootPath;
    private final ObjectMapper objectMapper;

    @Autowired
    public StatsFilesInfrastructure(@Qualifier("data.path") Path rootPath, ObjectMapper objectMapper) {
        this.rootPath = rootPath;
        this.objectMapper = objectMapper;
    }

    @Cacheable(CACHE_KEY)
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

    @CacheEvict(value = CACHE_KEY, allEntries = true)
    public void clearCache() {}

    public Path getRootPath() {
        return rootPath;
    }

    private FullGame toFullGame(Path path) {
        try {
            return objectMapper.readValue(Files.newInputStream(path), FullGame.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

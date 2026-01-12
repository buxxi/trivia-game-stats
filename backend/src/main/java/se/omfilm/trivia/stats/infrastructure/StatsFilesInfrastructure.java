package se.omfilm.trivia.stats.infrastructure;

import tools.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Map;

@Service
public class StatsFilesInfrastructure {
    public static final String GAMES_CACHE_KEY = "readAllGames";
    public static final String ALIAS_CACHE_KEY = "readAliases";
    public static final String ALIAS_FILENAME = "aliases.json";

    private final Path rootPath;
    private final ObjectMapper objectMapper;

    @Autowired
    public StatsFilesInfrastructure(@Qualifier("data.path") Path rootPath, ObjectMapper objectMapper) {
        this.rootPath = rootPath;
        this.objectMapper = objectMapper;
    }

    @Cacheable(GAMES_CACHE_KEY)
    public Collection<FullGame> readAllGames() {
        try (var files = Files.list(rootPath)) {
            return files
                    .parallel()
                    .filter(p -> p.toString().endsWith(".json") && !p.toString().endsWith(ALIAS_FILENAME))
                    .map(this::toFullGame)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable(ALIAS_CACHE_KEY)
    public Map<String, List<String>> readAliases() {
        Path path = aliasPath();
        if (!Files.exists(path)) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(Files.newInputStream(path), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @CacheEvict(value = ALIAS_CACHE_KEY, allEntries = true)
    public void writeAliases(Map<String, List<String>> aliasMap) {
        try {
            objectMapper.writeValue(Files.newOutputStream(aliasPath()), aliasMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @CacheEvict(value = GAMES_CACHE_KEY, allEntries = true)
    public void clearCache() {}

    private Path aliasPath() {
        return rootPath.resolve(ALIAS_FILENAME);
    }

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

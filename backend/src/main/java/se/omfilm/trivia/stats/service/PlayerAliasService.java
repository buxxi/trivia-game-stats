package se.omfilm.trivia.stats.service;

import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

@Service
public class PlayerAliasService {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final StatsFilesInfrastructure statsFilesInfrastructure;

    public PlayerAliasService(StatsFilesInfrastructure statsFilesInfrastructure) {
        this.statsFilesInfrastructure = statsFilesInfrastructure;
    }

    public Optional<String> getMainName(String gameName) {
        lock.readLock().lock();
        try {
            return resolveAlias(gameName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<String> getAliases(String mainName) {
        lock.readLock().lock();
        try {
            return loadAliasMap().getOrDefault(mainName, List.of());
        } finally {
            lock.readLock().unlock();
        }
    }
    public void setAliases(String name, List<String> aliases) {
        lock.writeLock().lock();
        try {
            if (isAlias(name)) {
                throw new IllegalStateException(name + " is already used as an alias");
            }
            Map<String, List<String>> aliasMap = loadAliasMap();
            if (aliases.isEmpty()) {
                aliasMap.remove(name);
            } else {
                aliasMap.put(name, aliases);
            }
            writeAliasMap(aliasMap);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isAlias(String name) {
        return resolveAlias(name).isPresent();
    }

    private Map<String, List<String>> loadAliasMap() {
        return new HashMap<>(statsFilesInfrastructure.readAliases());
    }

    private void writeAliasMap(Map<String, List<String>> aliasMap) {
        statsFilesInfrastructure.writeAliases(aliasMap);
    }

    private Optional<String> resolveAlias(String gameName) {
        return loadAliasMap().entrySet().stream()
                .filter(e -> e.getValue().contains(gameName))
                .map(Map.Entry::getKey)
                .findAny();
    }
}

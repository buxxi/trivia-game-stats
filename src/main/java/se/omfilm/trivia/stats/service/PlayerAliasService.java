package se.omfilm.trivia.stats.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

@Service
public class PlayerAliasService {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Map<String, List<String>> ALIAS_MAP = new HashMap<>(); //TODO: make actual infrastructure

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
            return ALIAS_MAP.getOrDefault(mainName, List.of());
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addAliases(String name, List<String> aliases) {
        lock.writeLock().lock();
        try {
            if (isAlias(name)) {
                throw new IllegalStateException(name + " is already used as an alias");
            }
            ALIAS_MAP.merge(name, aliases, (a, b) -> Stream.concat(a.stream(), b.stream()).toList());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeAliases(String name, List<String> aliases) {
        lock.writeLock().lock();
        try {
            if (isAlias(name)) {
                throw new IllegalStateException(name + " is already used as an alias");
            }
            ALIAS_MAP.put(name, ALIAS_MAP.getOrDefault(name, List.of()).stream().filter(alias -> !aliases.contains(alias)).toList());
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isAlias(String name) {
        return resolveAlias(name).isPresent();
    }

    private Optional<String> resolveAlias(String gameName) {
        return ALIAS_MAP.entrySet().stream()
                .filter(e -> e.getValue().contains(gameName))
                .map(Map.Entry::getKey)
                .findAny();
    }
}

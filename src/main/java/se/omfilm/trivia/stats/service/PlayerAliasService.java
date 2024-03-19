package se.omfilm.trivia.stats.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerAliasService {
    public Optional<String> getAlias(String gameId, String name) {
        return Optional.empty();
    }
}

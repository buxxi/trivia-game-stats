package se.omfilm.trivia.stats.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerAliasService {
    public Optional<String> getMainName(String gameName) {
        return Optional.empty();
    }

    public List<String> getAliases(String mainName) {
        return List.of();
    }
}

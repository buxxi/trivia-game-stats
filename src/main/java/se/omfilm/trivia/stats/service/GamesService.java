package se.omfilm.trivia.stats.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;

@Service
public class GamesService {
    private final StatsFilesInfrastructure statsFilesInfrastructure;

    @Autowired
    public GamesService(StatsFilesInfrastructure statsFilesInfrastructure) {
        this.statsFilesInfrastructure = statsFilesInfrastructure;
    }
}

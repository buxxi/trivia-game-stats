package se.omfilm.trivia.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;

@Service
public class CategoriesService {
    private final StatsFilesInfrastructure statsFilesInfrastructure;

    @Autowired
    public CategoriesService(StatsFilesInfrastructure statsFilesInfrastructure) {
        this.statsFilesInfrastructure = statsFilesInfrastructure;
    }
}

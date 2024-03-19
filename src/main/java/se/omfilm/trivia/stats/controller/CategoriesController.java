package se.omfilm.trivia.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.omfilm.trivia.stats.controller.io.CategorySummaryResponse;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;

import java.util.List;

@RestController
public class CategoriesController {
    private final StatsFilesInfrastructure statsFilesInfrastructure;

    @Autowired
    public CategoriesController(StatsFilesInfrastructure statsFilesInfrastructure) {
        this.statsFilesInfrastructure = statsFilesInfrastructure;
    }

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CategorySummaryResponse> getAllCategories() {
        throw new UnsupportedOperationException("Not implemented"); //TODO
    }
}

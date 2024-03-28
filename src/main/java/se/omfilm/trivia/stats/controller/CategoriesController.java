package se.omfilm.trivia.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.omfilm.trivia.stats.controller.io.CategorySummaryResponse;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;
import se.omfilm.trivia.stats.service.CategoriesService;

import java.util.List;

@RestController
public class CategoriesController {
    private final CategoriesService categoriesService;

    @Autowired
    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CategorySummaryResponse> getAllCategories() {
        return categoriesService.getAllCategories().stream()
                .map(CategorySummaryResponse::new)
                .toList();
    }
}

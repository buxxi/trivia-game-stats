package se.omfilm.trivia.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.omfilm.trivia.stats.domain.CategorySummary;
import se.omfilm.trivia.stats.domain.GuessCount;
import se.omfilm.trivia.stats.domain.GuessOption;
import se.omfilm.trivia.stats.domain.SingleGuess;
import se.omfilm.trivia.stats.infrastructure.StatsFilesInfrastructure;
import se.omfilm.trivia.stats.infrastructure.io.FullGame;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoriesService {
    private final StatsFilesInfrastructure statsFilesInfrastructure;

    @Autowired
    public CategoriesService(StatsFilesInfrastructure statsFilesInfrastructure) {
        this.statsFilesInfrastructure = statsFilesInfrastructure;
    }

    public List<CategorySummary> getAllCategories() {
        Collection<FullGame> allGames = statsFilesInfrastructure.readAllGames();
        GuessCount totals = getGuessTotals(allGames);
        return allGames
                .stream()
                .map(FullGame::questions)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(FullGame.Question::category))
                .entrySet()
                .stream()
                .map(category -> toCategorySummary(category, totals))
                .sorted(Comparator.comparing(CategorySummary::rating).reversed())
                .toList();
    }

    private CategorySummary toCategorySummary(Map.Entry<String, List<FullGame.Question>> category, GuessCount totals) {
        GuessCount guessCount = toGuessCount(category);

        return CategorySummary.of(
                category.getKey(),
                category.getValue().size(),
                guessCount,
                totals
        );
    }

    private static GuessCount toGuessCount(Map.Entry<String, List<FullGame.Question>> category) {
        return category.getValue().stream()
                .map(FullGame.Question::guesses)
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(g -> toSingleGuess(g, category.getKey()))
                .map(SingleGuess::toGuessCount)
                .reduce(GuessCount.EMPTY, GuessCount::merge);
    }

    private static SingleGuess toSingleGuess(FullGame.Question.Guess g, String category) {
        return new SingleGuess(GuessOption.valueOf(g.guessed()), g.correct(), g.time(), g.multiplier(), g.points(), category);
    }

    private GuessCount getGuessTotals(Collection<FullGame> allGames) {
        return allGames.stream()
                .map(FullGame::questions)
                .flatMap(Collection::stream)
                .map(FullGame.Question::guesses)
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(g -> toSingleGuess(g, null))
                .map(SingleGuess::toGuessCount)
                .reduce(GuessCount.EMPTY, GuessCount::merge);
    }
}

package se.omfilm.trivia.stats.util;

import java.math.BigDecimal;

public class BayesianEstimate {
    public static BigDecimal calculate(float percentage, long count, float averagePercentage, long minimumCount) {
        float percentageRating = (count / (float) (count + minimumCount)) * percentage;
        float averagePercentageRating = (minimumCount / (float) (count + minimumCount)) * averagePercentage;
        return new BigDecimal(percentageRating + averagePercentageRating);
    }
}

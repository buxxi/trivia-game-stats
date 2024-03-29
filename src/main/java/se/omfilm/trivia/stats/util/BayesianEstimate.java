package se.omfilm.trivia.stats.util;

import se.omfilm.trivia.stats.domain.Percentable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BayesianEstimate {
    public static BigDecimal calculate(Percentable current, Percentable totals, int minimumCount) {
        float percentage = current.percentage().floatValue();
        return calculate(percentage, current.total(), totals.percentage().floatValue(), minimumCount);
    }

    public static BigDecimal calculate(float percentage, long count, float averagePercentage, long minimumCount) {
        float percentageRating = (count / (float) (count + minimumCount)) * percentage;
        float averagePercentageRating = (minimumCount / (float) (count + minimumCount)) * averagePercentage;
        return new BigDecimal(percentageRating + averagePercentageRating).setScale(2, RoundingMode.UP);
    }
}

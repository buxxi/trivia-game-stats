package se.omfilm.trivia.stats.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface Percentable {
    int count();

    int total();

    default BigDecimal percentage() {
        return BigDecimal.valueOf(count())
                .setScale(2, RoundingMode.UP)
                .divide(BigDecimal.valueOf(total()), RoundingMode.UP)
                .multiply(BigDecimal.valueOf(100));
    }
}

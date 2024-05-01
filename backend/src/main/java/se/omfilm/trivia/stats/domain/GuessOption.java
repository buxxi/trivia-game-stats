package se.omfilm.trivia.stats.domain;

public enum GuessOption {
    A,
    B,
    C,
    D,
    UNANSWERED;

    public static GuessOption from(String value) {
        if (value == null) {
            return UNANSWERED;
        }
        for (GuessOption option : values()) {
            if (option.toString().equals(value)) {
                return option;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid value for " + GuessOption.class.getName());
    }
}

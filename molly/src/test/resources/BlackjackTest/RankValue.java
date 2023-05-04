package io.github.tysonmcnulty;

import java.lang.String;

public enum RankValue {
    ACE("ace"),

    TWO("two"),

    THREE("three"),

    FOUR("four"),

    FIVE("five"),

    SIX("six"),

    SEVEN("seven"),

    EIGHT("eight"),

    NINE("nine"),

    TEN("ten"),

    JACK("jack"),

    QUEEN("queen"),

    KING("king");

    private final String label;

    private RankValue(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

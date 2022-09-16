package io.github.tysonmcnulty;

import java.lang.String;

public enum Suit {
    CLUB("club"),

    DIAMOND("diamond"),

    HEART("heart"),

    SPADE("spade");

    private final String label;

    private Suit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

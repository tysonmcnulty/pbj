package com.vmware.example;

import java.lang.String;

public enum Rank {
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

    private Rank(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
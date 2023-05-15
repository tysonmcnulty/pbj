package io.github.tysonmcnulty;


public enum SuitValue {
    CLUB("club"),

    DIAMOND("diamond"),

    HEART("heart"),

    SPADE("spade");

    private final String label;

    private SuitValue(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

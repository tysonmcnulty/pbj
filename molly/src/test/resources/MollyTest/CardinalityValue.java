package io.github.tysonmcnulty;


public enum CardinalityValue {
    ONE_TO_ONE("one to one"),

    ONE_TO_MANY("one to many");

    private final String label;

    private CardinalityValue(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

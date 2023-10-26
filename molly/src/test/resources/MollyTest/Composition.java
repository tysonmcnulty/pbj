package io.github.tysonmcnulty;

public class Composition extends Relation<Unit, Composer> {
    protected CardinalityValue cardinality;

    protected boolean isCategorical;

    public CardinalityValue getCardinality() {
        return cardinality;
    }

    public boolean isCategorical() {
        return isCategorical;
    }
}

package io.github.tysonmcnulty;

public class Composition extends Relation<Unit, Composer> {
    protected CardinalityValue cardinality;

    public CardinalityValue getCardinality() {
        return cardinality;
    }
}

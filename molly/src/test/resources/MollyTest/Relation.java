package io.github.tysonmcnulty;

public abstract class Relation<MutationType extends Term, OperatorType extends Operator> {
    protected Unit mutant;

    protected MutationType mutation;

    protected OperatorType operator;

    public Unit getMutant() {
        return mutant;
    }

    public MutationType getMutation() {
        return mutation;
    }

    public OperatorType getOperator() {
        return operator;
    }
}

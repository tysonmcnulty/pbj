package io.github.tysonmcnulty;

public class Relation<T extends Term, T extends Operator> {
    protected Unit mutant;

    protected T mutation;

    protected T operator;

    public Unit getMutant() {
        return mutant;
    }

    public T getMutation() {
        return mutation;
    }

    public T getOperator() {
        return operator;
    }
}

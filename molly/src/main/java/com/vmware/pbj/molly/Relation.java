package com.vmware.pbj.molly;

public abstract class Relation<T, U> {
    protected Term mutant;
    protected T operand;
    protected U mutation;

    public Term getMutant() {
        return mutant;
    }

    public T getOperand() {
        return operand;
    }

    public U getMutation() {
        return mutation;
    }

    protected Relation(Term mutant, T operand, U mutation) {
        this.mutant = mutant;
        this.operand = operand;
        this.mutation = mutation;
    }
}

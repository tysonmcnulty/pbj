package com.vmware.pbj.molly;

public abstract class Relation<T, U> {
    private Term mutant;
    private T operand;
    private U mutation;

    public Term getMutant() {
        return mutant;
    }

    public void setMutant(Term mutant) {
        this.mutant = mutant;
    }

    public T getOperand() {
        return operand;
    }

    public void setOperand(T operand) {
        this.operand = operand;
    }

    public U getMutation() {
        return mutation;
    }

    public void setMutation(U mutation) {
        this.mutation = mutation;
    }

    protected Relation(Term mutant, T operand, U mutation) {
        this.mutant = mutant;
        this.operand = operand;
        this.mutation = mutation;
    }
}

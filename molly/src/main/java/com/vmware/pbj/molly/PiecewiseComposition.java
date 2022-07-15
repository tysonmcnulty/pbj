package com.vmware.pbj.molly;

public class PiecewiseComposition extends Composition {
    protected PiecewiseComposition() {
        super(null, null, null);
    }

    public void setMutant(Term mutant) {
        this.mutant = mutant;
    }

    public void setOperand(Composer operand) {
        this.operand = operand;
    }

    public void setMutation(Term mutation) {
        this.mutation = mutation;
    }
}

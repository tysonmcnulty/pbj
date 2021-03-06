package com.vmware.pbj.molly;

public final class PiecewiseCategorization extends Categorization {
    public PiecewiseCategorization() {
        super(null, null, null);
    }

    public void setMutant(Term mutant) {
        this.mutant = mutant;
    }

    public void setOperand(Categorizer operand) {
        this.operand = operand;
    }

    public void setMutation(Term mutation) {
        this.mutation = mutation;
    }
}

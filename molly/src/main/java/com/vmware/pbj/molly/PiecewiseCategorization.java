package com.vmware.pbj.molly;

public class PiecewiseCategorization extends Categorization{
    protected PiecewiseCategorization() {
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

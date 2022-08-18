package com.vmware.pbj.molly;

public class PiecewiseDescription extends Description {
    protected PiecewiseDescription() {
        super(null, null, null);
    }

    public void setMutant(Term mutant) {
        this.mutant = mutant;
    }

    public void setOperand(Describer operand) {
        this.operand = operand;
    }

    public void setMutation(Term mutation) {
        this.mutation = mutation;
    }
}

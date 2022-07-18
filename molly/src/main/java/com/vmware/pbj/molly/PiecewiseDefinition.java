package com.vmware.pbj.molly;

public class PiecewiseDefinition extends Definition {
    protected PiecewiseDefinition() {
        super(null, null, null);
    }

    public void setMutant(Term mutant) {
        this.mutant = mutant;
    }

    public void setOperand(Definer operand) {
        this.operand = operand;
    }

    public void setMutation(Term mutation) {
        this.mutation = mutation;
    }
}

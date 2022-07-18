package com.vmware.pbj.molly;

import java.util.ArrayList;

public class PiecewiseDescription extends Description {
    protected PiecewiseDescription() {
        super(null, null, new ArrayList<>());
    }

    public void setMutant(Term mutant) {
        this.mutant = mutant;
    }

    public void setOperand(Describer operand) {
        this.operand = operand;
    }

    public void addDescriptor(String descriptor) {
        this.mutation.add(descriptor);
    }
}

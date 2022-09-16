package com.vmware.pbj.molly;

public class PiecewiseDescription extends Description {
    public void setMutant(Term mutant) {
        this.mutant = mutant;
    }

    public void setRelater(Describer relater) {
        this.relater = relater;
    }

    public void setMutation(Term mutation) {
        this.mutation = mutation;
    }
}

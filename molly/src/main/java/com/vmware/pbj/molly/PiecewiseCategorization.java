package com.vmware.pbj.molly;

public final class PiecewiseCategorization extends Categorization {
    public void setMutant(Term mutant) {
        this.mutant = mutant;
    }

    public void setRelater(Categorizer relater) {
        this.relater = relater;
    }

    public void setMutation(Term mutation) {
        this.mutation = mutation;
    }
}

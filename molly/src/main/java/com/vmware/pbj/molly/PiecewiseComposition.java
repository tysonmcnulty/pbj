package com.vmware.pbj.molly;

public class PiecewiseComposition extends Composition {
    public void setMutant(Term mutant) {
        this.mutant = mutant;
    }

    public void setRelater(Composer relater) {
        this.relater = relater;
    }

    public void setMutation(Term mutation) {
        this.mutation = mutation;
    }
}

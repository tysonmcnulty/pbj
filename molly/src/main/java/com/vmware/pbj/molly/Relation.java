package com.vmware.pbj.molly;

public abstract class Relation<T> {
    protected Term mutant;
    protected T relater;
    protected Term mutation;

    public Term getMutant() {
        return mutant;
    }

    public T getRelater() {
        return relater;
    }

    public Term getMutation() {
        return mutation;
    }
}

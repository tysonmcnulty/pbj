package io.github.tysonmcnulty;

public abstract class Relation<T> {
    protected Term mutant;

    protected Term mutation;

    protected T relater;

    public Term getMutant() {
        return mutant;
    }

    public Term getMutation() {
        return mutation;
    }

    public T getRelater() {
        return relater;
    }
}

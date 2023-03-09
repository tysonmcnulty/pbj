package com.vmware.pbj.molly.core;

import java.util.Objects;

public abstract class Relation<T extends Operand, U extends Term> {
    Unit mutant;
    T operand;
    U mutation;

    public T getOperand() {
        return operand;
    };

    public Term getMutant() {
        return mutant;
    }

    public U getMutation() {
        return mutation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relation<?, ?> relation = (Relation<?, ?>) o;
        return mutant.equals(relation.mutant) && operand.equals(relation.operand) && mutation.equals(relation.mutation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mutant, operand, mutation);
    }

    @Override
    public String toString() {
        return "Relation{" +
            "mutant=" + mutant +
            ", operand=" + operand +
            ", mutation=" + mutation +
            '}';
    }
}

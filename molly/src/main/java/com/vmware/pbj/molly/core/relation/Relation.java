package com.vmware.pbj.molly.core.relation;

import com.vmware.pbj.molly.core.operator.Operator;
import com.vmware.pbj.molly.core.term.Term;
import com.vmware.pbj.molly.core.term.Unit;

import java.util.Objects;

public abstract class Relation<T extends Operator, U extends Term> {
    Unit mutant;
    T operator;
    U mutation;

    public T getOperator() {
        return operator;
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
        return mutant.equals(relation.mutant) && operator.equals(relation.operator) && mutation.equals(relation.mutation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mutant, operator, mutation);
    }

    @Override
    public String toString() {
        return "Relation{" +
            "mutant=" + mutant +
            ", operator=" + operator +
            ", mutation=" + mutation +
            '}';
    }
}

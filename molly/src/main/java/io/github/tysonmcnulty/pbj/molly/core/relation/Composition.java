package io.github.tysonmcnulty.pbj.molly.core.relation;

import io.github.tysonmcnulty.pbj.molly.core.operator.Composer;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;

import java.util.Objects;

public class Composition extends Relation<Composer, Unit> {
    private final Cardinality cardinality;
    private final boolean isCategorical;

    public Composition(
        Unit mutant,
        Unit mutation
    ) {
        this(mutant, new Composer(), mutation, Cardinality.ONE_TO_ONE, false);
    }

    public Composition(
        Unit mutant,
        Composer composer,
        Unit mutation,
        Cardinality cardinality,
        boolean isCategorical
    ) {
        this.mutant = mutant;
        this.operator = composer;
        this.mutation = mutation;
        this.cardinality = cardinality;
        this.isCategorical = isCategorical;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public boolean isCategorical() {
        return isCategorical;
    }

    public static class Builder {
        private final Unit mutant;
        private Composer composer = new Composer();
        private final Unit mutation;
        private Cardinality cardinality = Cardinality.ONE_TO_ONE;
        private boolean isCategorical = false;

        public Builder(
            Unit mutant,
            Unit mutation
        ) {

            this.mutant = mutant;
            this.mutation = mutation;
        }

        public Builder composer(Composer composer) {
            this.composer = composer;
            return this;
        }

        public Builder cardinality(Cardinality cardinality) {
            this.cardinality = cardinality;
            return this;
        }

        public Builder categorical(boolean isCategorical) {
            this.isCategorical = isCategorical;
            return this;
        }

        public Composition build() {
            return new Composition(mutant, composer, mutation, cardinality, isCategorical);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Composition that = (Composition) o;
        return isCategorical == that.isCategorical && cardinality == that.cardinality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cardinality, isCategorical);
    }

    @Override
    public String toString() {
        return "Composition{" +
            "cardinality=" + cardinality +
            ", isCategorical=" + isCategorical +
            ", mutant=" + mutant +
            ", operator=" + operator +
            ", mutation=" + mutation +
            '}';
    }
}

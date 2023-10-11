package io.github.tysonmcnulty.pbj.molly.core.relation;

import io.github.tysonmcnulty.pbj.molly.core.operator.Categorizer;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;

public class Categorization extends Relation<Categorizer, Unit> {

    public Categorization(
        Unit mutant,
        Unit mutation
    ) {
        this(mutant, new Categorizer(), mutation);
    }

    public Categorization(
        Unit mutant,
        Categorizer categorizer,
        Unit mutation
    ) {
        this.mutant = mutant;
        this.operator = categorizer;
        this.mutation = mutation;
    }
}

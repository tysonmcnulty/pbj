package io.github.tysonmcnulty.pbj.molly.core.relation;

import io.github.tysonmcnulty.pbj.molly.core.operator.Definer;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;

public class Definition extends Relation<Definer, Unit> {

    public Definition(Unit mutant, Unit mutation) {
        this.mutant = mutant;
        this.operator = new Definer();
        this.mutation = mutation;
    }
}

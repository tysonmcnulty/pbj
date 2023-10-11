package io.github.tysonmcnulty.pbj.molly.core.relation;

import io.github.tysonmcnulty.pbj.molly.core.operator.Describer;
import io.github.tysonmcnulty.pbj.molly.core.term.Descriptor;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;

public class Description extends Relation<Describer, Descriptor> {

    public Description(Unit mutant, Descriptor mutation) {
        this(mutant, new Describer(), mutation);
    }

    public Description(Unit mutant, Describer operator, Descriptor mutation) {
        this.mutant = mutant;
        this.operator = operator;
        this.mutation = mutation;
    }
}

package com.vmware.pbj.molly.core;

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
        this.operand = categorizer;
        this.mutation = mutation;
    }
}

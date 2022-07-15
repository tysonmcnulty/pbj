package com.vmware.pbj.molly;

public abstract class Categorization extends Relation<Categorizer, Term> {
    protected Categorization(Term mutant, Categorizer operand, Term mutation) {
        super(mutant, operand, mutation);
    }
}

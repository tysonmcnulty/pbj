package com.vmware.pbj.molly;

public class Composition extends Relation<Composer, Term> {
    protected Composition(Term mutant, Composer operand, Term mutation) {
        super(mutant, operand, mutation);
    }
}

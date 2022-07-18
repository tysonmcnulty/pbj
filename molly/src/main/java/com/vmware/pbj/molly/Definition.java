package com.vmware.pbj.molly;

public abstract class Definition extends Relation<Definer, Term> {
    protected Definition(Term mutant, Definer operand, Term mutation) {
        super(mutant, operand, mutation);
    }
}

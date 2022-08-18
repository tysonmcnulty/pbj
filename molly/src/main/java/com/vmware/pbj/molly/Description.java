package com.vmware.pbj.molly;

public abstract class Description extends Relation<Describer, Term> {
    protected Description(Term mutant, Describer operand, Term mutation) {
        super(mutant, operand, mutation);
    }
}

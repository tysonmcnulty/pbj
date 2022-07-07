package com.vmware.pbj.molly;

public class Composition extends Relation<Term, Composer> {
    protected Composition(Term mutant, Term mutation, Composer operand) {
        super(mutant, mutation, operand);
    }
}

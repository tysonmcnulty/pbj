package com.vmware.pbj.molly.core;

public class Description extends Relation<Describer, Descriptor> {

    public Description(Unit mutant, Descriptor mutation) {
        this(mutant, new Describer(), mutation);
    }

    public Description(Unit mutant, Describer operand, Descriptor mutation) {
        this.mutant = mutant;
        this.operand = operand;
        this.mutation = mutation;
    }
}

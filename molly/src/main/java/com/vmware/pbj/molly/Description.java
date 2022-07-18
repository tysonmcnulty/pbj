package com.vmware.pbj.molly;

import java.util.List;

public abstract class Description extends Relation<Describer, List<String>> {
    protected Description(Term mutant, Describer operand, List<String> mutation) {
        super(mutant, operand, mutation);
    }
}

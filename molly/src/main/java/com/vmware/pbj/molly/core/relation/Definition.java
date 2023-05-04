package com.vmware.pbj.molly.core.relation;

import com.vmware.pbj.molly.core.operator.Definer;
import com.vmware.pbj.molly.core.term.Unit;

public class Definition extends Relation<Definer, Unit> {

    public Definition(Unit mutant, Unit mutation) {
        this.mutant = mutant;
        this.operator = new Definer();
        this.mutation = mutation;
    }
}

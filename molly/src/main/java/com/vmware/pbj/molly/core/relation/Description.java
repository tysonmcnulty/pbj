package com.vmware.pbj.molly.core.relation;

import com.vmware.pbj.molly.core.operator.Describer;
import com.vmware.pbj.molly.core.term.Descriptor;
import com.vmware.pbj.molly.core.term.Unit;

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

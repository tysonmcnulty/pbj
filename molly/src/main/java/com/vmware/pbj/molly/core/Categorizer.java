package com.vmware.pbj.molly.core;

public class Categorizer extends Operand {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 1;
    }
}

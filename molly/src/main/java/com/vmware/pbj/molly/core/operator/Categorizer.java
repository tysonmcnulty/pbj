package com.vmware.pbj.molly.core.operator;

public class Categorizer extends Operator {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "Categorizer{}";
    }
}

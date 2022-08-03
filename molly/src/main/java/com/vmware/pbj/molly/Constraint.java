package com.vmware.pbj.molly;

public class Constraint {
    private final String[] values;

    public Constraint(String... values) {
        this.values = values;
    }

    public String[] getValues() {
        return values;
    }
}

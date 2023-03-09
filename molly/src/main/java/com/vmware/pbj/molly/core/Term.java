package com.vmware.pbj.molly.core;

import java.util.Objects;

public abstract class Term {
    String name;

    public Term(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return name.equals(term.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

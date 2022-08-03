package com.vmware.pbj.molly;

import java.util.Objects;
import java.util.Optional;

public class Term {
    private final String name;
    private String representation;
    private Constraint constraint = null;

    public Term(String name) {
        this.representation = this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return Objects.equals(name, term.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Optional<Constraint> getValueConstraint() {
        return Optional.ofNullable(constraint);
    }

    public void setValueConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }
}

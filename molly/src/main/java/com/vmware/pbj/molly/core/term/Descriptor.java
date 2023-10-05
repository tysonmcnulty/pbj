package com.vmware.pbj.molly.core.term;

import java.util.Objects;
import java.util.Optional;

public class Descriptor extends Term {
    private final String negation;

    public Descriptor(String name) {
        this(name, null);
    }

    public Descriptor(String name, String negation) {
        super(name);
        this.negation = negation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Descriptor that = (Descriptor) o;
        return Objects.equals(negation, that.negation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), negation);
    }

    @Override
    public String toString() {
        return "Descriptor{" +
            "negation='" + negation + '\'' +
            ", name='" + name + '\'' +
            '}';
    }

    public Optional<String> getNegation() {
        return Optional.ofNullable(negation);
    }
}

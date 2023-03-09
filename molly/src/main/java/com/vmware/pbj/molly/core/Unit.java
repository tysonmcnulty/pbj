package com.vmware.pbj.molly.core;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.vmware.pbj.molly.EnglishUtils.inflectionsOf;

public class Unit extends Term {

    private String context;

    public Unit(String name) {
        super(name);
    }

    public Optional<String> getContext() {
        return Optional.ofNullable(context);
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isPrimitive() {
        return primitives.contains(this);
    }

    public String getPluralName() {
        return inflectionsOf(name)[1];
    }

    public static Unit fromInflectedName(String inflectedName) {
        return new Unit(inflectionsOf(inflectedName)[0]);
    }

    @Override
    public String toString() {
        return "Unit{" +
            "name='" + name + '\'' +
            (context != null ? ",context='" + context + '\'' : "") +
            '}';
    }

    public static final Set<Unit> primitives = Set.of(
        new Unit("string"),
        new Unit("number"),
        new Unit("decimal"),
        new Unit("boolean")
    );

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Unit unit = (Unit) o;
        return Objects.equals(context, unit.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), context);
    }
}

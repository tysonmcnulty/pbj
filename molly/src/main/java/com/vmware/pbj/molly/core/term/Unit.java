package com.vmware.pbj.molly.core.term;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
        return primitives.containsValue(this);
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

    private static final Map<String, Unit> singularPrimitives = Map.of(
        "string", new Unit("string"),
        "number", new Unit("number"),
        "decimal", new Unit("decimal"),
        "boolean", new Unit("boolean")
    );

    public static final Map<String, Unit> primitives = Map.of(
        "string", singularPrimitives.get("string"),
        "strings", singularPrimitives.get("string"),
        "number", singularPrimitives.get("number"),
        "numbers", singularPrimitives.get("number"),
        "decimal", singularPrimitives.get("decimal"),
        "decimals", singularPrimitives.get("decimal"),
        "boolean", singularPrimitives.get("boolean"),
        "booleans", singularPrimitives.get("boolean")
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

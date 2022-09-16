package com.vmware.pbj.molly;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Term {
    private Constraint constraint;
    private final String name;
    private Language language;

    private final static Set<String> primitiveNames = Set.of(
            "string",
            "number",
            "decimal",
            "boolean"
    );

    public Term(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isPrimitive() {
        return primitiveNames.contains(name);
    }

    public Optional<Constraint> getConstraint() {
        return Optional.ofNullable(constraint);
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public Optional<Language> getLanguage() {
        return Optional.ofNullable(language);
    }

    public void setLanguage(Language language) {
        this.language = language;
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
}

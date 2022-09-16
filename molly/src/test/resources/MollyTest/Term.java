package io.github.tysonmcnulty;

import java.lang.String;
import java.util.Optional;

public abstract class Term {
    protected String name;

    protected Language language;

    protected Constraint constraint;

    public String getName() {
        return name;
    }

    public Optional<Language> getLanguage() {
        return Optional.ofNullable(language);
    }

    public Optional<Constraint> getConstraint() {
        return Optional.ofNullable(constraint);
    }

    public abstract boolean isPrimitive();
}

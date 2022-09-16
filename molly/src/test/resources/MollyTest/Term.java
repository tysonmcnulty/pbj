package io.github.tysonmcnulty;

import java.lang.String;
import java.util.Optional;

public abstract class Term {
    protected String name;

    protected Language language;

    protected ValueConstraint valueConstraint;

    public String getName() {
        return name;
    }

    public Optional<Language> getLanguage() {
        return Optional.ofNullable(language);
    }

    public Optional<ValueConstraint> getValueConstraint() {
        return Optional.ofNullable(valueConstraint);
    }

    public abstract boolean isPrimitive();
}

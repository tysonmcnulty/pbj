package io.github.tysonmcnulty;

import java.util.Optional;

public abstract class Unit extends Term {
    protected String context;

    public abstract boolean isPrimitive();

    public Optional<String> getContext() {
        return Optional.ofNullable(context);
    }
}

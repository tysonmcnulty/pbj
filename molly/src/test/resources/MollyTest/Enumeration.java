package io.github.tysonmcnulty;

import java.util.Collection;

public abstract class Enumeration extends Unit {
    protected Collection<String> values;

    public Collection<String> getValues() {
        return values;
    }
}

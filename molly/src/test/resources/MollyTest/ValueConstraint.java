package io.github.tysonmcnulty;

import java.lang.String;
import java.util.Collection;

public abstract class ValueConstraint {
    protected Collection<String> values;

    public Collection<String> getValues() {
        return values;
    }
}

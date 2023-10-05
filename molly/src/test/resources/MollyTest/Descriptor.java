package io.github.tysonmcnulty;

import java.util.Optional;

public class Descriptor extends Term {
    protected String negation;

    public Optional<String> getNegation() {
        return Optional.ofNullable(negation);
    }
}

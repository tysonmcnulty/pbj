package io.github.tysonmcnulty;

import java.util.Collection;

public abstract class Language {
    protected Collection<Term> terms;

    protected Collection<Relation> relations;

    public Collection<Term> getTerms() {
        return terms;
    }

    public Collection<Relation> getRelations() {
        return relations;
    }
}

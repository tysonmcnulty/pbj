package io.github.tysonmcnulty;

import java.util.Collection;

public class Language {
    protected Collection<Term> terms;

    protected Collection<Descriptor> descriptors;

    protected Collection<Relation> relations;

    public Collection<Term> getTerms() {
        return terms;
    }

    public Collection<Descriptor> getDescriptors() {
        return descriptors;
    }

    public Collection<Relation> getRelations() {
        return relations;
    }
}

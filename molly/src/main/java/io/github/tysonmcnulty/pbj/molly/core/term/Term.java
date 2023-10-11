package io.github.tysonmcnulty.pbj.molly.core.term;

import java.util.Objects;

import static io.github.tysonmcnulty.pbj.molly.EnglishUtils.inflectionsOf;

public abstract class Term {
    String name;

    public Term(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPluralName() {
        return inflectionsOf(name)[1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return name.equals(term.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

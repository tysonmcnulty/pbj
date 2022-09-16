package com.vmware.pbj.molly;

import java.util.*;
import java.util.stream.Collectors;

import static com.vmware.pbj.molly.EnglishUtils.inflectionsOf;
import static java.util.function.Predicate.not;

public class Language {
    Map<String, Term> termsByName = new LinkedHashMap<>();
    Set<Composition> compositions = new LinkedHashSet<>();
    Set<Categorization> categorizations = new LinkedHashSet<>();
    Set<Description> descriptions = new LinkedHashSet<>();

    public Collection<Term> getTerms() {
        return termsByName.values();
    }

    public Collection<Term> getStandaloneTerms() {
        var predefinedTerms = getCategorizations().stream()
                .filter(c -> c.getRelater().equals(Categorizer.IS_JUST))
                .map(Categorization::getMutant)
                .collect(Collectors.toSet());

        return getTerms().stream()
                .filter(not(Term::isPrimitive))
                .filter(not(predefinedTerms::contains))
                .collect(Collectors.toSet());
    }

    public Collection<Composition> getCompositions() {
        return compositions;
    }

    public Collection<Categorization> getCategorizations() {
        return categorizations;
    }

    public Collection<Description> getDescriptions() {
        return descriptions;
    }

    public void addTermByName(String termName) {
        String[] inflections = inflectionsOf(termName);
        var singularTermName = inflections[0];
        var pluralTermName = inflections[1];
        var term = new Term(singularTermName);
        termsByName.putIfAbsent(singularTermName, term);
        termsByName.putIfAbsent(pluralTermName, term);
    }

    public Term getTermByName(String termName) {
        return termsByName.get(termName);
    }

    public void addComposition(Composition composition) {
        compositions.add(composition);
    }

    public void addCategorization(Categorization categorization) {
        categorizations.add(categorization);
    }

    public void addDescription(Description description) {
        descriptions.add(description);
    }
}

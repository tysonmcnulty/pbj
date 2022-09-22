package com.vmware.pbj.molly;

import java.util.*;
import java.util.stream.Collectors;

import static com.vmware.pbj.molly.EnglishUtils.inflectionsOf;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

public class Language {
    Map<String, Term> termsByName = new LinkedHashMap<>();
    Set<Composition> compositions = new LinkedHashSet<>();
    Set<Categorization> categorizations = new LinkedHashSet<>();
    Set<Description> descriptions = new LinkedHashSet<>();

    public Collection<Term> getTerms() {
        return termsByName.values();
    }

    public Collection<Term> getStandaloneTerms() {
        var aliases = getCategorizations().stream()
                .filter(c -> Set.of(Categorizer.IS_JUST, Categorizer.ARE_JUST).contains(c.getRelater()))
                .map(Categorization::getMutant)
                .collect(toSet());

        var descriptors = getDescriptions().stream()
                .map(Description::getMutation)
                .collect(toSet());

        var abstractions = getCompositions().stream()
                .filter(c -> c.getRelater().getVerb().equals(Composer.Verb.HAS_SOME_KIND_OF))
                .map(Composition::getMutation)
                .collect(toSet());

        return getTerms().stream()
                .filter(not(Term::isPrimitive))
                .filter(not(aliases::contains))
                .filter(not(descriptors::contains))
                .filter(not(abstractions::contains))
                .collect(toSet());
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

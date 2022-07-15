package com.vmware.pbj.molly;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;
import java.util.stream.Collectors;

class MollyInterpreter extends MollyBaseListener {

    Map<String, Term> termsByName = new HashMap<>();
    Set<Composition> compositions = new HashSet<>();

    public Collection<Term> getTerms() {
        return termsByName.values();
    }

    @Override
    public void enterTerm(MollyParser.TermContext ctx) {
        String termName = getText(ctx);
        String[] inflectedTermNames = EnglishUtils.inflectionsOf(termName);
        var singular = inflectedTermNames[0];
        var plural = inflectedTermNames[1];
        var term = new Term(singular);
        termsByName.putIfAbsent(singular, term);
        termsByName.putIfAbsent(plural, term);
    }

    @Override
    public void exitComposition(MollyParser.CompositionContext ctx) {
        PiecewiseComposition composition = new PiecewiseComposition();
        composition.setMutant(termsByName.get(getText(ctx.term(0))));
        composition.setOperand(Composer.valueOfLabel(ctx.COMPOSER().getText()));
        composition.setMutation(termsByName.get(getText(ctx.term(1))));
        compositions.add(composition);
    }

    private String getText(MollyParser.TermContext ctx) {
        return ctx.WORD().stream().map(ParseTree::getText).collect(Collectors.joining(" ")).toLowerCase();
    }

    public Collection<Composition> getCompositions() {
        return compositions;
    }
}

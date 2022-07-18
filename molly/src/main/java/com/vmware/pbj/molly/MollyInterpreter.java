package com.vmware.pbj.molly;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.stream.Collectors;

class MollyInterpreter extends MollyBaseListener {

    Map<String, Term> termsByName = new HashMap<>();

    Set<Composition> compositions = new HashSet<>();
    Set<Categorization> categorizations = new HashSet<>();
    Set<Description> descriptions = new HashSet<>();

    public Collection<Term> getTerms() {
        return termsByName.values();
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

    @Override
    public void enterTerm(MollyParser.TermContext ctx) {
        addTerm(ctx.WORD());
    }

    @Override
    public void enterCategory(MollyParser.CategoryContext ctx) {
        addTerm(ctx.WORD());
    }

    private void addTerm(List<TerminalNode> word) {
        String termName = getText(word);
        String[] inflectedTermNames = EnglishUtils.inflectionsOf(termName);
        var singular = inflectedTermNames[0];
        var plural = inflectedTermNames[1];
        var term = new Term(singular);
        termsByName.putIfAbsent(singular, term);
        termsByName.putIfAbsent(plural, term);
    }

    @Override
    public void exitComposition(MollyParser.CompositionContext ctx) {
        var composition = new PiecewiseComposition();
        composition.setMutant(termsByName.get(getText(ctx.term(0).WORD())));
        composition.setOperand(Composer.valueOfLabel(ctx.COMPOSER().getText()));
        composition.setMutation(termsByName.get(getText(ctx.term(1).WORD())));
        compositions.add(composition);
    }

    @Override
    public void exitDescription(MollyParser.DescriptionContext ctx) {
        var description = new PiecewiseDescription();
        description.setMutant(termsByName.get(getText(ctx.term().WORD())));
        description.setOperand(Describer.valueOfLabel(ctx.DESCRIBER().getText()));
        ctx.descriptor().forEach((d) -> {
            description.addDescriptor(getText(d.WORD()));
        });
        descriptions.add(description);
    }

    @Override
    public void exitCategorization(MollyParser.CategorizationContext ctx) {
        var categorization = new PiecewiseCategorization();
        categorization.setMutant(termsByName.get(getText(ctx.term().WORD())));
        categorization.setOperand(Categorizer.valueOfLabel(ctx.CATEGORIZER().getText()));
        categorization.setMutation(termsByName.get(getText(ctx.category().get(0).WORD())));
        categorizations.add(categorization);
    }

    private String getText(List<TerminalNode> words) {
        return words.stream().map(ParseTree::getText).collect(Collectors.joining(" ")).toLowerCase();
    }
}

package com.vmware.pbj.molly;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.stream.Collectors;

class MollyInterpreter extends MollyBaseListener {

    Map<String, Term> termsByName = new LinkedHashMap<>();

    Set<Composition> compositions = new LinkedHashSet<>();
    Set<Categorization> categorizations = new LinkedHashSet<>();
    Set<Description> descriptions = new LinkedHashSet<>();

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

    private Term addTerm(List<TerminalNode> word) {
        String termName = getText(word);
        String[] inflectedTermNames = EnglishUtils.inflectionsOf(termName);
        var singular = inflectedTermNames[0];
        var plural = inflectedTermNames[1];
        var term = new Term(singular);
        termsByName.putIfAbsent(singular, term);
        termsByName.putIfAbsent(plural, term);
        return term;
    }

    @Override
    public void exitComposition(MollyParser.CompositionContext ctx) {
        var composition = new PiecewiseComposition();
        composition.setMutant(termsByName.get(getText(ctx.term(0).WORD())));
        composition.setOperand(new Composer(
                Composer.Verb.labeled(ctx.composer().COMPOSER_VERB().getText()),
                ctx.composer().QUALIFIER() == null ? null : Qualifier.labeled(ctx.composer().QUALIFIER().getText())
        ));
        composition.setMutation(termsByName.get(getText(ctx.term(1).WORD())));
        compositions.add(composition);
    }

    @Override
    public void exitCategorization(MollyParser.CategorizationContext ctx) {

        var mutant = termsByName.get(getText(ctx.term().WORD()));
        var operand = Categorizer.labeled(ctx.CATEGORIZER().getText());
        var category = getText(ctx.category().WORD());

        if (operand == Categorizer.IS_JUST) {
            mutant.setRepresentation(Representation.labeled(category));
        } else {
            var categorization = new PiecewiseCategorization();
            categorization.setMutant(mutant);
            categorization.setOperand(operand);
            var term = addTerm(ctx.category().WORD());
            categorization.setMutation(term);
            categorizations.add(categorization);
        }
    }

    @Override
    public void exitDescription(MollyParser.DescriptionContext ctx) {
        var description = new PiecewiseDescription();
        description.setMutant(termsByName.get(getText(ctx.term().get(0).WORD())));
        description.setOperand(Describer.labeled(ctx.DESCRIBER().getText()));
        description.setMutation(termsByName.get(getText(ctx.term().get(1).WORD())));
        descriptions.add(description);
    }

    @Override
    public void exitEnumeration(MollyParser.EnumerationContext ctx) {
        var term = termsByName.get(getText(ctx.term().WORD()));
        var values = ctx.value()
            .stream().map(it -> getText(it.value().WORD())).toArray(String[]::new);
        term.setValueConstraint(new Constraint(values));
    }

    private String getText(List<TerminalNode> words) {
        return words.stream().map(ParseTree::getText).collect(Collectors.joining(" ")).toLowerCase();
    }
}

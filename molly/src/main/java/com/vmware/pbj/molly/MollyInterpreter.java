package com.vmware.pbj.molly;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.stream.Collectors;

class MollyInterpreter extends MollyBaseListener {

    private final Language rootLanguage = new Language();
    private final Deque<Language> languageStack = new ArrayDeque<>(List.of(rootLanguage));

    public Language getLanguage() {
        return languageStack.getFirst();
    }

    @Override
    public void exitTerm(MollyParser.TermContext ctx) {
        String givenTermName = getText(ctx.WORD());
        if (givenTermName.isEmpty()) return;

        rootLanguage.addTermByName(givenTermName);
    }

    @Override
    public void exitComposition(MollyParser.CompositionContext ctx) {
        var composition = new PiecewiseComposition();
        composition.setMutant(rootLanguage.getTermByName(getText(ctx.term(0).WORD())));
        composition.setRelater(new Composer(
                Composer.Verb.labeled(ctx.composer().COMPOSER_VERB().getText()),
                ctx.composer().QUALIFIER() == null ? null : Qualifier.labeled(ctx.composer().QUALIFIER().getText())
        ));
        composition.setMutation(rootLanguage.getTermByName(getText(ctx.term(1).WORD())));
        getLanguage().addComposition(composition);
    }

    @Override
    public void exitCategorization(MollyParser.CategorizationContext ctx) {
        var mutant = rootLanguage.getTermByName(getText(ctx.term(0).WORD()));
        var relater = Categorizer.labeled(ctx.CATEGORIZER().getText());
        var mutation = rootLanguage.getTermByName(getText(ctx.term(1).WORD()));

        var categorization = new PiecewiseCategorization();
        categorization.setMutant(mutant);
        categorization.setRelater(relater);
        categorization.setMutation(mutation);
        getLanguage().addCategorization(categorization);
    }

    @Override
    public void enterSubcategorization(MollyParser.SubcategorizationContext ctx) {
        if (Subordinator.labeled(ctx.SUBORDINATOR().getText()) == Subordinator.WHEREIN) {
            var outerTerm = rootLanguage.getTermByName(getText(
                    ((MollyParser.Relation_declarationContext) ctx.getParent().getParent())
                            .relation().categorization().term(0).WORD()));
            var language = outerTerm.getLanguage().orElse(new Language());
            outerTerm.setLanguage(language);
            languageStack.push(language);
        }
    }

    @Override
    public void exitSubcategorization(MollyParser.SubcategorizationContext ctx) {
        Language language = getLanguage();
        Term mutant;
        Categorizer relater;
        PiecewiseCategorization categorization;
        Term mutation;

        switch (Subordinator.labeled(ctx.SUBORDINATOR().getText())) {

            case WHEREIN:
                mutant = rootLanguage.getTermByName(getText(ctx.term(0).WORD()));
                relater = Categorizer.labeled(ctx.CATEGORIZER().getText());
                mutation = rootLanguage.getTermByName(getText(ctx.term(1).term().WORD()));

                categorization = new PiecewiseCategorization();
                categorization.setMutant(mutant);
                categorization.setRelater(relater);
                categorization.setMutation(mutation);
                language.addCategorization(categorization);

                languageStack.pop();

                break;
            case WHICH:
                mutant = language.getTermByName(getText(
                        ((MollyParser.CompositionContext)
                            ((MollyParser.Relation_declarationContext)
                                ctx.getParent().getParent()
                            ).relation().getChild(0)
                        ).term(1).WORD()));
                relater = Categorizer.labeled(ctx.CATEGORIZER().getText());
                mutation = rootLanguage.getTermByName(getText(ctx.term(0).WORD()));

                categorization = new PiecewiseCategorization();
                categorization.setMutant(mutant);
                categorization.setRelater(relater);
                categorization.setMutation(mutation);
                language.addCategorization(categorization);
                break;
        }
    }

    @Override
    public void exitDescription(MollyParser.DescriptionContext ctx) {
        var description = new PiecewiseDescription();
        description.setMutant(rootLanguage.getTermByName(getText(ctx.term().get(0).WORD())));
        description.setRelater(Describer.labeled(ctx.DESCRIBER().getText()));
        description.setMutation(rootLanguage.getTermByName(getText(ctx.term().get(1).WORD())));
        getLanguage().addDescription(description);
    }

    @Override
    public void exitEnumeration(MollyParser.EnumerationContext ctx) {
        var term = rootLanguage.getTermByName(getText(ctx.term().WORD()));
        var values = ctx.value()
            .stream().map(it -> getText(it.value().WORD())).toArray(String[]::new);
        term.setConstraint(new Constraint(values));
    }

    private String getText(List<TerminalNode> words) {
        return words.stream().map(ParseTree::getText).collect(Collectors.joining(" ")).toLowerCase();
    }
}

package io.github.tysonmcnulty.pbj.molly;

import io.github.tysonmcnulty.pbj.molly.core.Language;
import io.github.tysonmcnulty.pbj.molly.core.operator.Composer;
import io.github.tysonmcnulty.pbj.molly.core.operator.Describer;
import io.github.tysonmcnulty.pbj.molly.core.relation.*;
import io.github.tysonmcnulty.pbj.molly.core.term.Descriptor;
import io.github.tysonmcnulty.pbj.molly.core.term.Enumeration;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MollyListenerInterpreter extends MollyBaseListener {

    private Language language;

    public Language getLanguage() {
        return language;
    }

    public Language read(InputStream source) {
        MollyLexer lexer = lex(source);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MollyParser parser = new MollyParser(tokenStream);
        parser.setBuildParseTree(true);

        ParseTreeWalker.DEFAULT.walk(this, parser.file());
        return language;
    }

    private static MollyLexer lex(InputStream in) {
        try {
            CharStream cStream = CharStreams.fromStream(in);
            return new MollyLexer(cStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void enterFile(MollyParser.FileContext ctx) {
        this.language = new Language();
    }

    @Override
    public void exitUnit(MollyParser.UnitContext ctx) {
        String termNameCandidate = termNameOf(ctx.term());
        if (termNameCandidate.isEmpty()) return;

        var unit = Unit.fromInflectedName(termNameCandidate);
        if (ctx.context() != null) {
            unit.setContext(termNameOf(ctx.context().term()));
        }

        this.language.addUnit(unit);
    }

    @Override
    public void exitValues(MollyParser.ValuesContext ctx) {
        var surroundingContext = ctx.getParent().getParent();
        String associatedTermName = null;

        if (surroundingContext instanceof MollyParser.DefinitionContext) {
            associatedTermName = termNameOf(
                ((MollyParser.DefinitionContext) surroundingContext).mutant.unit().term()
            );
        } else if (surroundingContext instanceof MollyParser.SubdefinitionContext) {
            associatedTermName = getSubordinatedMutationName(
                (MollyParser.SubdefinitionContext) surroundingContext);
        }

        var enumeration = new Enumeration.Builder(String.format("%s value", associatedTermName))
            .values(ctx.value().stream().map(this::valueOf).toArray(String[]::new))
            .build();

        this.language.addUnit(enumeration);
    }

    private String getSubordinatedMutationName(MollyParser.SubdefinitionContext surroundingContext) {
        var outerRelationDeclarationContext = (MollyParser.Relation_declarationContext) surroundingContext.getParent().getParent();
        var associatedRelationContext = outerRelationDeclarationContext.relation();
        return termNameOf(
            findContext(associatedRelationContext, MollyParser.UnitContext.class).get().term()
        );
    }

    @Override
    public void exitDefinition(MollyParser.DefinitionContext ctx) {
        Unit mutant = language.getUnitByName(unitNameOf(ctx.mutant.unit()));
        Unit mutation = ctx.mutation.values() == null
            ? language.getUnitByName(unitNameOf(ctx.mutation.unit()))
            : language.getUnitByName(String.format("%s values", mutant.getName()));

        this.language.addRelation(new Definition(mutant, mutation));
    }

    @Override
    public void exitDescription_mutation(MollyParser.Description_mutationContext ctx) {
        String descriptorNameCandidate = termNameOf(ctx.descriptor().term());
        if (descriptorNameCandidate.isEmpty()) return;

        var negationValue = valueOf(ctx.negation().value());
        String negation = List.of(
            "not",
            String.join(" ", "not", descriptorNameCandidate))
        .contains(negationValue)
            ? null
            : negationValue;

        this.language.addDescriptor(new Descriptor(descriptorNameCandidate, negation));
    }

    @Override
    public void exitDescription(MollyParser.DescriptionContext ctx) {
        Unit mutant = language.getUnitByName(unitNameOf(ctx.mutant.unit()));
        Describer operator = new Describer(
            ctx.operator.QUALIFIER() != null,
            ctx.operator.OBVIATOR() != null
        );
        Descriptor mutation = language.getDescriptorByName(termNameOf(ctx.mutation.descriptor().term()));

        language.addRelation(new Description(mutant, operator, mutation));
    }

    @Override
    public void exitCategorization(MollyParser.CategorizationContext ctx) {
        var mutant = language.getUnitByName(unitNameOf(ctx.mutant.unit()));
        var mutation = language.getUnitByName(unitNameOf(ctx.mutation.category().unit()));

        language.addRelation(new Categorization(mutant, mutation));
    }

    @Override
    public void exitComposition(MollyParser.CompositionContext ctx) {
        var mutant = language.getUnitByName(unitNameOf(ctx.mutant.unit()));
        var operator = new Composer(
            ctx.operator.QUALIFIER() != null,
            ctx.operator.OBVIATOR() != null
        );
        var cardinality = ctx.MULTIPLIER() == null
            ? Cardinality.ONE_TO_ONE
            : Cardinality.ONE_TO_MANY;

        if (ctx.mutation.category() != null) {
            var mutation = language.getUnitByName(unitNameOf(ctx.mutation.category().unit()));
            language.addRelation(new Composition.Builder(mutant, mutation)
                .composer(operator)
                .cardinality(cardinality)
                .categorical(true)
                .build());
        } else if (ctx.mutation.unit() != null) {
            var mutation = language.getUnitByName(unitNameOf(ctx.mutation.unit()));
            language.addRelation(new Composition.Builder(mutant, mutation)
                .composer(operator)
                .cardinality(cardinality)
                .build());
        }
    }

    @Override
    public void exitSubdefinition(MollyParser.SubdefinitionContext ctx) {
        var mutant = language.getUnitByName(getSubordinatedMutationName(ctx));
        Unit mutation = ctx.mutation.values() == null
            ? language.getUnitByName(unitNameOf(ctx.mutation.unit()))
            : language.getUnitByName(String.format("%s values", mutant.getName()));

        language.addRelation(new Definition(mutant, mutation));
    }

    private String termNameOf(MollyParser.TermContext ctx) {
        String termName;
        if (ctx.BOXED_WORDS() != null) {
            var boxedWordsText = ctx.BOXED_WORDS().getText();
            termName = EnglishUtils.normalizeCase(String.join(" ", boxedWordsText.substring(1, boxedWordsText.length() - 1).split("\\s+")));
        } else {
            termName = EnglishUtils.normalizeCase(ctx.WORD().stream().map(ParseTree::getText).collect(Collectors.joining(" ")));
        }
        return termName;
    }

    private String unitNameOf(MollyParser.UnitContext ctx) {
        var termName = termNameOf(ctx.term());
        String contextTermName = null;
        if (ctx.context() != null) {
            contextTermName = termNameOf(ctx.context().term());
        }
        return contextTermName != null ? contextTermName + " " + termName : termName;
    }

    private String valueOf(MollyParser.ValueContext ctx) {
        if (ctx.BOXED_WORDS() != null) {
            var boxedWordsText = ctx.BOXED_WORDS().getText();
            return EnglishUtils.normalizeCase(String.join(" ", boxedWordsText.substring(1, boxedWordsText.length() - 1).split("\\s+")));
        } else {
            return EnglishUtils.normalizeCase(ctx.WORD().stream().map(ParseTree::getText).collect(Collectors.joining(" ")));
        }
    }

    private <T extends ParserRuleContext> Optional<T> findContext(ParserRuleContext outerContext, Class<T> tClass) {

        final T[] context = (T[]) Array.newInstance(tClass, 1);

        MollyBaseListener adhocListener = new MollyBaseListener() {
            @Override
            public void exitEveryRule(ParserRuleContext ctx) {
                if (ctx.getClass().equals(tClass)) {
                    context[0] = (T) ctx;
                }
            }
        };

        ParseTreeWalker.DEFAULT.walk(adhocListener, outerContext);
        return Optional.ofNullable(context[0]);
    }
}

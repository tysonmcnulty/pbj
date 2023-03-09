package com.vmware.pbj.molly;

import com.vmware.pbj.molly.core.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
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
    public void exitDescription_mutation(MollyParser.Description_mutationContext ctx) {
        String descriptorNameCandidate = termNameOf(ctx.descriptor().term());
        if (descriptorNameCandidate.isEmpty()) return;

        String negation = valueOf(ctx.negation().value());

        this.language.addDescriptor(new Descriptor(descriptorNameCandidate, negation));
    }

    @Override
    public void exitDescription(MollyParser.DescriptionContext ctx) {
        Unit mutant = language.getUnitByName(termNameOf(ctx.mutant.unit().term()));
        Describer operand = new Describer(
            ctx.operand.QUALIFIER() != null,
            ctx.operand.OBVIATOR() != null
        );
        Descriptor mutation = language.getDescriptorByName(termNameOf(ctx.mutation.descriptor().term()));

        language.addRelation(new Description(mutant, operand, mutation));
    }

    @Override
    public void exitCategorization(MollyParser.CategorizationContext ctx) {
        var mutant = language.getUnitByName(termNameOf(ctx.mutant.unit().term()));
        var mutation = language.getUnitByName(termNameOf(ctx.mutation.category().unit().term()));

        language.addRelation(new Categorization(mutant, mutation));
    }

    @Override
    public void exitComposition(MollyParser.CompositionContext ctx) {
        var mutant = language.getUnitByName(termNameOf(ctx.mutant.unit().term()));
        var operand = new Composer(
            ctx.operand.QUALIFIER() != null,
            ctx.operand.OBVIATOR() != null
        );
        var cardinality = ctx.MULTIPLIER() == null
            ? Cardinality.ONE_TO_ONE
            : Cardinality.ONE_TO_MANY;

        if (ctx.mutation.category() != null) {
            var mutation = language.getUnitByName(termNameOf(ctx.mutation.category().unit().term()));
            language.addRelation(new Composition.Builder(mutant, mutation)
                .composer(operand)
                .cardinality(cardinality)
                .categorical(true)
                .build());
        } else if (ctx.mutation.unit() != null) {
            var mutation = language.getUnitByName(termNameOf(ctx.mutation.unit().term()));
            language.addRelation(new Composition.Builder(mutant, mutation)
                .composer(operand)
                .cardinality(cardinality)
                .build());
        }
    }

    private String termNameOf(MollyParser.TermContext ctx) {
        if (ctx.BOXED_WORDS() != null) {
            var boxedWordsText = ctx.BOXED_WORDS().getText();
            return EnglishUtils.normalizeCase(String.join(" ", boxedWordsText.substring(1, boxedWordsText.length() - 1).split("\\s+")));
        } else {
            return EnglishUtils.normalizeCase(ctx.WORD().stream().map(ParseTree::getText).collect(Collectors.joining(" ")));
        }
    }

    private String valueOf(MollyParser.ValueContext ctx) {
        if (ctx.BOXED_WORDS() != null) {
            var boxedWordsText = ctx.BOXED_WORDS().getText();
            return EnglishUtils.normalizeCase(String.join(" ", boxedWordsText.substring(1, boxedWordsText.length() - 1).split("\\s+")));
        } else {
            return EnglishUtils.normalizeCase(ctx.WORD().stream().map(ParseTree::getText).collect(Collectors.joining(" ")));
        }
    }
}

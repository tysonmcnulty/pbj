package com.vmware.pbj.molly;

import com.hypertino.inflector.English;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.*;

import javax.lang.model.element.Modifier;
import javax.swing.text.html.parser.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class MollyJavaGenerator {

    public static final String PACKAGE = "com.vmware.example";
    private MollyParser parser;
    private MollyListener listener;

    public void read(InputStream source) {
        MollyLexer lexer = lex(source);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        parser = new MollyParser(tokenStream);
        parser.setBuildParseTree(true);
    }

    public void process() {
        listener = new MollyListener();
        ParseTreeWalker.DEFAULT.walk(listener, parser.file());
    }

    public void write(Path dir) {
        try {
            for (String termName: new HashSet<>(listener.getTerms().values())) {
                TypeSpec typeSpec = TypeSpec.classBuilder(capitalize(termName))
                        .addModifiers(Modifier.PUBLIC)
                        .build();
                JavaFile javaFile = JavaFile.builder(PACKAGE, typeSpec).build();
                System.out.printf("Writing %s to %s%n", capitalize(termName), dir);
                javaFile.writeToPath(dir);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static MollyLexer lex(InputStream in) {
        try {
            CharStream cStream = CharStreams.fromStream(in);
            return new MollyLexer(cStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private class MollyListener implements ParseTreeListener {

        Map<String, String> terms = new HashMap<>();

        public Map<String, String> getTerms() {
            return terms;
        }

        @Override
        public void visitTerminal(TerminalNode node) {

        }

        @Override
        public void visitErrorNode(ErrorNode node) {

        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
            if (ctx instanceof MollyParser.TermContext) {
                MollyParser.TermContext termContext = (MollyParser.TermContext) ctx;
                String termName = termContext.WORD().stream().map(ParseTree::getText).collect(Collectors.joining(" "));
                String[] singularAndPluralTermName = EnglishUtils.singularAndPlural(termName.toLowerCase());
                String singular = singularAndPluralTermName[0];
                String plural = singularAndPluralTermName[1];
                terms.putIfAbsent(singular, singular);
                terms.putIfAbsent(plural, singular);
            }
        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {

        }
    }
}

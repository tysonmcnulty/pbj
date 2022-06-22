package com.vmware.pbj.molly;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.indexOfSubList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

public class MollyTest {

    List<Token> tokens = tokenize("kitchen.molly");

    @Test
    void lexer_tokenizes_relationships() {
        assertTrue(tokens.size() > 0);

        assertNotEquals(-1, indexOfSubList(
                tokens.stream().map(Token::getText).collect(toList()),
                asList("-", "kitchen", "has", "countertop")));

        assertNotEquals(-1, indexOfSubList(
                tokens.stream().map(Token::getText).collect(toList()),
                asList("-", "pantry", "has many", "foods")));
    }

    @Test
    void parse_tree_has_all_declarations() {
        MollyParser.FileContext fileContext = parse("kitchen.molly");
        assertEquals(5, fileContext.term_declaration().size());
        assertEquals(4, fileContext.relationship_declaration().size());
    }

    private MollyLexer lex(String resourceName) {
        InputStream iStream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resourceName));
        CharStream cStream;
        try {
            cStream = CharStreams.fromStream(iStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new MollyLexer(cStream);
    }

    private List<Token> tokenize(String resourceName) {
        MollyLexer lexer = lex(resourceName);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new TestErrorListener());
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        return tokenStream.getTokens();
    }

    private MollyParser.FileContext parse(String resourceName) {
        MollyLexer lexer = lex(resourceName);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MollyParser parser = new MollyParser(tokenStream);
        return parser.file();
    }
}

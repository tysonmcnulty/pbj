package com.vmware.pbj.molly;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MollyLexerTest {

    @Test
    void lexer() {
        List<Token> tokens = tokenize("kitchen.molly");

        assertTrue(tokens.size() > 0);
    }

    private List<Token> tokenize(String resourceName) {
        InputStream iStream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resourceName));
        CharStream cStream;
        try {
            cStream = CharStreams.fromStream(iStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        MollyLexer lexer = new MollyLexer(cStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new TestErrorListener());
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        return tokenStream.getTokens();
    }
}

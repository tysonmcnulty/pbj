package com.vmware.pbj.molly;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
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

    @Test
    void writer_writes_a_java_class_file() throws IOException {
        Path tmpdir = Files.createTempDirectory(UUID.randomUUID().toString());
        MollyJavaGenerator generator = new MollyJavaGenerator();

        generator.read(resource("kitchen.molly"));
        generator.process();
        generator.write(tmpdir);

        for (String fileName : asList(
                "Countertop.java",
                "Kitchen.java"
        )) {
            String actualFileText = Files.readString(Paths.get(
                    tmpdir.toAbsolutePath().toString(),
                    "com", "vmware", "example",
                    fileName
            ));

            String expectedFileText = new BufferedReader(new InputStreamReader(
                    resource(Paths.get("MollyTest", fileName).toString()), UTF_8
            ))
                    .lines().collect(Collectors.joining("\n"));

            assertEquals(expectedFileText.trim(), actualFileText.trim());
        }
    }

    private static String randomTempDir() {
        try {
            return Files.createTempDirectory(UUID.randomUUID().toString()).toFile().getAbsolutePath();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private InputStream resource(String resourceName) {
        return Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resourceName));
    }

    private MollyLexer lex(String resourceName) {
        InputStream iStream = resource(resourceName);
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

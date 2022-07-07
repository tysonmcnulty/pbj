package com.vmware.pbj.molly;

import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.indexOfSubList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

public class MollyTest {

    List<Token> tokens = TestUtils.tokenize("kitchen.molly");

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
        MollyParser.FileContext fileContext = TestUtils.parse("kitchen.molly");
        assertEquals(5, fileContext.term_declaration().size());
        assertEquals(4, fileContext.relation_declaration().size());
    }

    @Test
    void writer_writes_a_java_class_file() throws IOException {
        Path tmpdir = Files.createTempDirectory(UUID.randomUUID().toString());
        MollyJavaGenerator generator = new MollyJavaGenerator();

        generator.read(TestUtils.resource("kitchen.molly"));
        generator.process();
        generator.write(tmpdir);

        Resource[] resources = TestUtils.resourcesMatching("classpath:/MollyTest/*.java");

        for (Resource r : resources) {
            String expectedFileText = new BufferedReader(new InputStreamReader(r.getInputStream(), UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            String actualFileText = Files.readString(Paths.get(
                    tmpdir.toAbsolutePath().toString(),
                    "com", "vmware", "example",
                    r.getFilename()
            ));

            assertEquals(expectedFileText.trim(), actualFileText.trim());
        }
    }
}

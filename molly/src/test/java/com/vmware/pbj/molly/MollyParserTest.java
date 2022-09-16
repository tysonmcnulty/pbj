package com.vmware.pbj.molly;

import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.indexOfSubList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

public class MollyParserTest {

    List<Token> tokens = TestUtils.tokenize("Kitchen.molly");

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
        MollyParser.FileContext fileContext = TestUtils.parse("Kitchen.molly");
        assertEquals(5, fileContext.term_declaration().size());
        assertEquals(4, fileContext.relation_declaration().size());
    }
}

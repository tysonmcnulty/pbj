package io.github.tysonmcnulty.pbj.molly;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MollyParserTest {

    @DisplayName("parse tree has all relation declarations")
    @ParameterizedTest(name = "resource: {0}.molly")
    @ValueSource(strings = {"PBJ", "Blackjack", "Molly", "Shelter"})
    void parse_tree_has_all_declarations(String languageName) {
        var resourceName = String.format("languages/%s.molly", languageName);
        var expectedNumberOfDeclarations = TestUtils.linesOf(resourceName)
            .filter(Pattern.compile("^-").asPredicate())
            .count();

        var fileContext = TestUtils.parse(resourceName);
        Assertions.assertEquals(expectedNumberOfDeclarations, fileContext.relation_declaration().size());
    }

    @DisplayName("parser returns the language")
    @ParameterizedTest(name = "resource: {0}.molly")
    @ValueSource(strings = {"PBJ", "Blackjack", "Molly", "Shelter"})
    void parser_returns_language(String languageName) {
        var interpreter = new MollyListenerInterpreter();
        var expected = TestLanguages.get(languageName);
        var actual = interpreter.read(TestUtils.resource(String.format("languages/%s.molly", languageName)));

        assertEquals(expected, actual);
    }
}

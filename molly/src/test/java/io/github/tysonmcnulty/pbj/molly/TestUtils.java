package io.github.tysonmcnulty.pbj.molly;

import com.vmware.pbj.molly.MollyLexer;
import com.vmware.pbj.molly.MollyParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class TestUtils {

    public static InputStream resource(String resourceName) {
        return Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
    }

    public static Stream<String> linesOf(String resourceName) {
        return new BufferedReader(new InputStreamReader(
            TestUtils.resource(resourceName)
        ))
            .lines();
    }

    public static MollyLexer lex(String resourceName) {
        InputStream iStream = resource(resourceName);
        CharStream cStream;
        try {
            cStream = CharStreams.fromStream(iStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new MollyLexer(cStream);
    }

    public static List<Token> tokenize(String resourceName) {
        MollyLexer lexer = lex(resourceName);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new TestErrorListener());
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        return tokenStream.getTokens();
    }

    public static MollyParser.FileContext parse(String resourceName) {
        MollyLexer lexer = lex(resourceName);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MollyParser parser = new MollyParser(tokenStream);
        return parser.file();
    }

    public static Resource[] resourcesMatching(String pattern) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            return resolver.getResources(pattern);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

package com.vmware.pbj.molly;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class MollyJavaGenerator {

    public static final String PACKAGE = "com.vmware.example";
    private MollyParser parser;
    private MollyInterpreter listener;

    public void read(InputStream source) {
        MollyLexer lexer = lex(source);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        parser = new MollyParser(tokenStream);
        parser.setBuildParseTree(true);
    }

    public void process() {
        listener = new MollyInterpreter();
        ParseTreeWalker.DEFAULT.walk(listener, parser.file());
    }

    public void write(Path dir) {
        try {
            Map<String, TypeSpec.Builder> buildersByTermName = listener.getTerms().stream()
                    .map((term) -> {
                        var typeSpecBuilder = TypeSpec
                            .classBuilder(capitalize(term.getName()))
                            .addModifiers(Modifier.PUBLIC)
                            .addModifiers(Modifier.ABSTRACT);

                        return Map.entry(term.getName(), typeSpecBuilder);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

            for (var composition: listener.getCompositions()) {
                var mutant = buildersByTermName.get(composition.getMutant().getName());
                var mutationName = composition.getMutation().getName();
                var mutationClassName = capitalize(mutationName);
                switch (composition.getOperand()) {
                    case HAS:
                        mutant
                                .addField(
                                        ClassName.get(PACKAGE, mutationClassName),
                                        composition.getMutation().getName(),
                                        Modifier.PROTECTED)
                                .addMethod(
                                        MethodSpec.methodBuilder("get" + mutationClassName)
                                                .addModifiers(Modifier.PUBLIC)
                                                .addStatement(String.format("return %s", mutationName))
                                                .returns(ClassName.get(PACKAGE, mutationClassName))
                                                .build());
                        break;
                    case HAS_MANY:
                        break;
                }
            }

            for (var builder: buildersByTermName.values()) {
                TypeSpec typeSpec = builder.build();
                JavaFile javaFile = JavaFile.builder(PACKAGE, typeSpec).build();
                System.out.printf("Writing %s to %s%n", capitalize(typeSpec.name), dir);
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
}
package com.vmware.pbj.molly;

import com.squareup.javapoet.*;
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

    public MollyJavaGenerator() {
        this(new MollyJavaGeneratorConfig.Builder().build());
    }

    private final MollyJavaGeneratorConfig config;
    private MollyInterpreter listener;

    public MollyJavaGenerator(MollyJavaGeneratorConfig config) {
        this.config = config;
    }

    public void read(InputStream source) {
        MollyLexer lexer = lex(source);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MollyParser parser = new MollyParser(tokenStream);
        parser.setBuildParseTree(true);
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

            processCompositions(buildersByTermName);
            processCategorizations(buildersByTermName);
            processDescriptions(buildersByTermName);

            for (var builder: buildersByTermName.values()) {
                TypeSpec typeSpec = builder.build();
                JavaFile javaFile = JavaFile.builder(config.getJavaPackage(), typeSpec).build();
                System.out.printf("Writing %s to %s%n", capitalize(typeSpec.name), dir);
                javaFile.writeToPath(dir);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void processDescriptions(Map<String, TypeSpec.Builder> buildersByTermName) {
        for (var description: listener.getDescriptions()) {
            var mutant = buildersByTermName.get(description.getMutant().getName());
            if (description.getOperand() == Describer.IS_EVIDENTLY) {
                if (EnglishUtils.isNegatedPair(description.getMutation())) {
                    mutant
                            .addMethod(MethodSpec
                                    .methodBuilder("is" + capitalize(description.getMutation().get(0)))
                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                    .returns(TypeName.BOOLEAN)
                                    .build()
                            );
                }
            }
        }
    }

    private void processCompositions(Map<String, TypeSpec.Builder> buildersByTermName) {
        for (var composition: listener.getCompositions()) {
            var mutant = buildersByTermName.get(composition.getMutant().getName());
            var mutationName = composition.getMutation().getName();
            var mutationClassName = capitalize(mutationName);
            switch (composition.getOperand()) {
                case HAS:
                    mutant
                            .addField(
                                    ClassName.get(config.getJavaPackage(), mutationClassName),
                                    mutationName,
                                    Modifier.PROTECTED)
                            .addMethod(
                                    MethodSpec.methodBuilder("get" + mutationClassName)
                                            .addModifiers(Modifier.PUBLIC)
                                            .addStatement(String.format("return %s", mutationName))
                                            .returns(ClassName.get(config.getJavaPackage(), mutationClassName))
                                            .build());
                    break;
                case HAS_MANY:
                    System.out.printf("Mutation name: %s\n", mutationName);
                    var pluralMutationName = EnglishUtils.inflectionsOf(mutationName)[1];
                    System.out.printf("Plural mutation name: %s\n", mutationName);
                    TypeName collectionType = ParameterizedTypeName.get(
                            ClassName.get("java.util", "Collection"),
                            ClassName.get(config.getJavaPackage(), mutationClassName));
                    mutant
                            .addField(
                                    collectionType,
                                    pluralMutationName,
                                    Modifier.PROTECTED)
                            .addMethod(
                                    MethodSpec.methodBuilder("get" + capitalize(pluralMutationName))
                                            .addModifiers(Modifier.PUBLIC)
                                            .addStatement(String.format("return %s", pluralMutationName))
                                            .returns(collectionType)
                                            .build());
                    break;
            }
        }
    }

    private void processCategorizations(Map<String, TypeSpec.Builder> buildersByTermName) {
        for (var categorization: listener.getCategorizations()) {
            var mutant = buildersByTermName.get(categorization.getMutant().getName());
            var mutationName = categorization.getMutation().getName();
            var mutationClassName = capitalize(mutationName);
            switch (categorization.getOperand()) {
                case IS_A_KIND_OF:
                case IS_A_TYPE_OF:
                    mutant.superclass(ClassName.get(config.getJavaPackage(), mutationClassName));
                    break;
            }
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

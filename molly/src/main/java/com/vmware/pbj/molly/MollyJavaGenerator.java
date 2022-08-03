package com.vmware.pbj.molly;

import com.google.common.base.Preconditions;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.squareup.javapoet.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class MollyJavaGenerator {

    public MollyJavaGenerator() {
        this(new MollyJavaGeneratorConfig.Builder().build());
    }

    private final MollyJavaGeneratorConfig config;
    private static final Formatter FORMATTER = new Formatter(JavaFormatterOptions.builder()
            .style(JavaFormatterOptions.Style.AOSP)
            .build());
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
            Map<String, TypeSpec.Builder> buildersByTermName = getStandaloneTerms().stream()
                    .map((term) -> {
                        if (term.getValueConstraint().isPresent()) {
                            var typeSpecBuilder = TypeSpec
                                    .enumBuilder(capitalize(term.getName()))
                                    .addModifiers(Modifier.PUBLIC);

                            var enumValues = term.getValueConstraint().get().getValues();

                            for (var v: enumValues) {
                                typeSpecBuilder.addEnumConstant(
                                        v.toUpperCase(),
                                        TypeSpec.anonymousClassBuilder("$S", v).build()
                                );
                            }

                            typeSpecBuilder
                                    .addField(String.class, "label", Modifier.PRIVATE, Modifier.FINAL)
                                    .addMethod(MethodSpec.constructorBuilder()
                                            .addModifiers(Modifier.PRIVATE)
                                            .addParameter(String.class, "label")
                                            .addStatement("this.label = label")
                                            .build())
                                    .addMethod(MethodSpec.methodBuilder("getLabel")
                                            .addModifiers(Modifier.PUBLIC)
                                            .addStatement("return label")
                                            .returns(String.class)
                                            .build());

                            return Map.entry(term.getName(), typeSpecBuilder);
                        } else {
                            var typeSpecBuilder = TypeSpec
                                    .classBuilder(capitalize(term.getName()))
                                    .addModifiers(Modifier.PUBLIC)
                                    .addModifiers(Modifier.ABSTRACT);

                            return Map.entry(term.getName(), typeSpecBuilder);
                        }

                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

            processCompositions(buildersByTermName);
            processCategorizations(buildersByTermName);
            processDescriptions(buildersByTermName);

            for (var builder: buildersByTermName.values()) {
                TypeSpec typeSpec = builder.build();
                JavaFile javaFile = JavaFile.builder(config.getJavaPackage(), typeSpec).build();
                Path outputFile = getFilePath(javaFile, dir);

                StringBuilder code = new StringBuilder();
                javaFile.writeTo(code);

                CharSink sink = com.google.common.io.Files.asCharSink(outputFile.toFile(), StandardCharsets.UTF_8);
                System.out.printf("Writing %s to %s%n", capitalize(typeSpec.name), dir);
                FORMATTER.formatSource(CharSource.wrap(code), sink);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (FormatterException fe) {

            throw new RuntimeException(fe);
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
                    var pluralMutationName = EnglishUtils.inflectionsOf(mutationName)[1];
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

    private Collection<Term> getStandaloneTerms() {
        var unneededTerms = listener.getCategorizations().stream()
                .filter(c -> c.getOperand().equals(Categorizer.IS_JUST))
                .map(Categorization::getMutant)
                .collect(Collectors.toSet());

        return listener.getTerms().stream()
                .filter(t -> !unneededTerms.contains(t))
                .collect(Collectors.toSet());
    }

    private static MollyLexer lex(InputStream in) {
        try {
            CharStream cStream = CharStreams.fromStream(in);
            return new MollyLexer(cStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Path getFilePath(JavaFile file, Path baseDir) throws IOException {
        Preconditions.checkArgument(Files.notExists(baseDir) || Files.isDirectory(baseDir),
                "path %s exists but is not a directory.", baseDir);
        Path outputDirectory = baseDir;
        if (!file.packageName.isEmpty()) {
            for (String packageComponent : file.packageName.split("\\.")) {
                outputDirectory = outputDirectory.resolve(packageComponent);
            }
            Files.createDirectories(outputDirectory);
        }

        return outputDirectory.resolve(file.typeSpec.name + ".java");
    }
}

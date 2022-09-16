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
import org.apache.commons.text.WordUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MollyJavaGenerator {

    public MollyJavaGenerator() {
        this(MollyJavaGeneratorConfig.builder().build());
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
            Map<String, TypeSpec.Builder> buildersByTermName = listener.getLanguage().getStandaloneTerms().stream()
                    .map((term) -> {
                        if (term.getConstraint().isPresent()) {
                            var typeSpecBuilder = TypeSpec
                                    .enumBuilder(classNameOf(term.getName()))
                                    .addModifiers(Modifier.PUBLIC);

                            var enumValues = term.getConstraint().get().getValues();

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
                            System.out.println("\"" + term.getName() + "\"");
                            var typeSpecBuilder = TypeSpec
                                    .classBuilder(classNameOf(term.getName()))
                                    .addModifiers(Modifier.PUBLIC)
                                    .addModifiers(Modifier.ABSTRACT);

                            return Map.entry(term.getName(), typeSpecBuilder);
                        }

                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

            processCategorizations(buildersByTermName);
            processCompositions(buildersByTermName);
            processDescriptions(buildersByTermName);

            for (var builder: buildersByTermName.values()) {
                TypeSpec typeSpec = builder.build();
                JavaFile javaFile = JavaFile.builder(config.getJavaPackage(), typeSpec).build();
                Path outputFile = getFilePath(javaFile, dir);

                StringBuilder code = new StringBuilder();
                javaFile.writeTo(code);

                CharSink sink = com.google.common.io.Files.asCharSink(outputFile.toFile(), StandardCharsets.UTF_8);
                System.out.printf("Writing %s to %s%n", classNameOf(typeSpec.name), dir);
                FORMATTER.formatSource(CharSource.wrap(code), sink);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (FormatterException fe) {

            throw new RuntimeException(fe);
        }
    }

    private void processDescriptions(Map<String, TypeSpec.Builder> buildersByTermName) {
        for (var description: listener.getLanguage().getDescriptions()) {
            var mutant = buildersByTermName.get(description.getMutant().getName());
            var mutation = description.getMutation();
            switch (description.getRelater()) {
                case EVIDENTLY_IS:
                    mutant
                            .addMethod(MethodSpec
                                    .methodBuilder("is" + classNameOf(mutation.getName()))
                                    .addModifiers(Modifier.PUBLIC)
                                    .addModifiers(Modifier.ABSTRACT)
                                    .returns(TypeName.BOOLEAN)
                                    .build()
                            );
                    break;
                case EVIDENTLY_HAS:
                    mutant
                            .addMethod(MethodSpec
                                    .methodBuilder("get" + classNameOf(mutation.getName()))
                                    .addModifiers(Modifier.PUBLIC)
                                    .addModifiers(Modifier.ABSTRACT)
                                    .returns(typeOf(mutation))
                                    .build()
                            );
            }
        }
    }

    private void processCompositions(Map<String, TypeSpec.Builder> buildersByTermName) {
        for (var composition: listener.getLanguage().getCompositions()) {
            var builder = buildersByTermName.get(composition.getMutant().getName());
            var mutation = composition.getMutation();
            var mutationName = mutation.getName();
            var mutationFieldName = fieldNameOf(mutationName);
            var mutationClassName = classNameOf(mutationName);
            switch (composition.getRelater().getVerb()) {
                case HAS:
                case HAVE:
                    builder.addField(
                            typeOf(mutation),
                            mutationFieldName,
                            Modifier.PROTECTED);

                    var accessor = MethodSpec.methodBuilder("get" + mutationClassName)
                                    .addModifiers(Modifier.PUBLIC);

                    if (composition.getRelater().getQualifier().isPresent()) {
                        TypeName optionalType = ParameterizedTypeName.get(
                                ClassName.get("java.util", "Optional"),
                                typeOf(mutation));
                        accessor
                                .addStatement(String.format("return Optional.ofNullable(%s)", mutationFieldName))
                                .returns(optionalType);
                    } else {
                        accessor
                                .addStatement(String.format("return %s", mutationFieldName))
                                .returns(typeOf(mutation));
                    }

                    builder.addMethod(accessor.build());
                    break;
                case HAS_MANY:
                case HAVE_MANY:
                    var pluralMutationName = EnglishUtils.inflectionsOf(mutationName)[1];
                    TypeName collectionType = ParameterizedTypeName.get(
                            ClassName.get("java.util", "Collection"),
                            typeOf(mutation));
                    builder
                            .addField(
                                    collectionType,
                                    pluralMutationName,
                                    Modifier.PROTECTED)
                            .addMethod(
                                    MethodSpec.methodBuilder("get" + classNameOf(pluralMutationName))
                                            .addModifiers(Modifier.PUBLIC)
                                            .addStatement(String.format("return %s", pluralMutationName))
                                            .returns(collectionType)
                                            .build());
                    break;
                case HAS_SOME_KIND_OF:
                    var genericType = TypeVariableName.get("T");

                    builder
                            .addTypeVariable(genericType)
                            .addField(
                                    genericType,
                                    mutationName,
                                    Modifier.PROTECTED)
                            .addMethod(MethodSpec.methodBuilder("get" + mutationClassName)
                                    .addModifiers(Modifier.PUBLIC)
                                    .addStatement(String.format("return %s", mutationName))
                                    .returns(genericType)
                                    .build()
                            )
                    ;
                    break;
            }
        }
    }

    private void processCategorizations(Map<String, TypeSpec.Builder> buildersByTermName) {
        for (var categorization: listener.getLanguage().getCategorizations()) {
            var mutant = buildersByTermName.get(categorization.getMutant().getName());
            var mutation = categorization.getMutation();
            var mutationName = mutation.getName();
            var mutationClassName = classNameOf(mutationName);
            switch (categorization.getRelater()) {
                case IS_A_KIND_OF:
                case IS_A_TYPE_OF:
                    TypeName supertype;
                    var genericComposition = listener.getLanguage().getCompositions().stream()
                            .filter((it) ->
                                    it.getMutant().equals(mutation) &&
                                    it.getRelater().getVerb().equals(Composer.Verb.HAS_SOME_KIND_OF))
                            .findFirst();

                    if (genericComposition.isPresent()) {
                        var innerCategorization = categorization.getMutant().getLanguage().get().getCategorizations().stream()
                                .filter((it) ->
                                        it.getMutant().equals(genericComposition.get().getMutation()) &&
                                        it.getRelater().equals(Categorizer.IS_JUST))
                                .findFirst();
                        supertype = ParameterizedTypeName.get(
                                ClassName.get(config.getJavaPackage(), mutationClassName),
                                ClassName.get(config.getJavaPackage(), classNameOf(innerCategorization.get().getMutation().getName()))
                        );
                    } else {
                        supertype = ClassName.get(config.getJavaPackage(), mutationClassName);
                    }
                    mutant.superclass(supertype);
                    break;
                case IS_JUST:
                case ARE_JUST:
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

    private TypeName typeOf(Term term) {
        var representation = resolveRepresentation(term);
        switch(representation) {
            case "string":
                return TypeName.get(String.class);
            case "number":
                return TypeName.get(int.class);
            case "decimal":
                return TypeName.get(double.class);
            case "boolean":
                return TypeName.get(boolean.class);
            default:
                return ClassName.get(config.getJavaPackage(), classNameOf(representation));
        }
    }

    private String classNameOf(String termName) {
        return WordUtils.capitalizeFully(termName).replaceAll("\\W", "");
    }

    private String fieldNameOf(String termName) {
        return WordUtils.uncapitalize(classNameOf(termName));
    }

    private String resolveRepresentation(Term term) {
        if (term.isPrimitive()) return term.getName();

        var resolution = listener.getLanguage().getCategorizations().stream()
                .filter((it) -> it.getMutant().equals(term) && Set.of(Categorizer.IS_JUST, Categorizer.ARE_JUST).contains(it.getRelater()))
                .findFirst();

        if (resolution.isPresent()) {
            return resolveRepresentation(resolution.get().getMutation());
        } else {
            return term.getName();
        }
    }
}

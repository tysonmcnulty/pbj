package io.github.tysonmcnulty.pbj.molly.write.java;

import com.diffplug.spotless.Provisioner;
import com.diffplug.spotless.java.GoogleJavaFormatStep;
import com.google.common.base.Preconditions;
import com.google.common.collect.RangeSet;
import com.google.common.io.CharSink;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.github.tysonmcnulty.pbj.molly.core.Language;
import io.github.tysonmcnulty.pbj.molly.core.relation.*;
import io.github.tysonmcnulty.pbj.molly.core.term.Enumeration;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.diffplug.spotless.Formatter.NO_FILE_SENTINEL;
import static com.google.common.io.Files.asCharSink;
import static io.github.tysonmcnulty.pbj.molly.write.java.Syntax.classNameOf;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class MollyJavaGenerator {

    public MollyJavaGenerator(Language language) {
        this(language, MollyJavaGeneratorConfig.builder().build());
    }

    private final MollyJavaGeneratorConfig config;
    private final Language language;

    public MollyJavaGenerator(Language language, MollyJavaGeneratorConfig config) {
        this.language = language;
        this.config = config;
    }

    public void write(Path dir) {
        try {
            var definitionMutants = language.getRelations().stream()
                .filter(r -> r instanceof Definition)
                .map(Relation::getMutant)
                .collect(Collectors.toSet());

            var unitsToWrite = language.getUnits()
                .filter((unit) -> !definitionMutants.contains(unit))
                .collect(Collectors.toList());

            Map<String, TypeSpec.Builder> buildersByUnitName = unitsToWrite.stream()
                .map((unit) -> Map.entry(unit.getUnitName(), createTypeSpecBuilder(unit)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

            language.getRelations().forEach((relation) -> apply(relation, buildersByUnitName));

            var categorizer = language.getCategorizer();

            var typeSpecs = buildersByUnitName.entrySet().stream()
                    .map(e -> Map.entry(e.getKey(), e.getValue().build()))
                    .collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

            unitsToWrite.forEach(unit -> {
                var parents = categorizer.apply(unit.getUnitName());
                var childrenMustBeAbstract = false;
                for (var i = parents.size() - 1; i >= 0; i--) {
                    var parentTypeSpec = typeSpecs.get(parents.get(i).getUnitName());
                    if (parentTypeSpec.hasModifier(Modifier.ABSTRACT)) {
                        childrenMustBeAbstract = true;
                    }
                    if (i > 0 || childrenMustBeAbstract) {
                        buildersByUnitName.get(parents.get(i).getUnitName())
                                .addModifiers(Modifier.ABSTRACT);
                    }
                }
            });

            unitsToWrite.forEach(unit -> {
                if (unit.getContext().isPresent()) {
                    var outerContextBuilder = buildersByUnitName.computeIfAbsent(
                        unit.getContext().get(),
                        contextName -> TypeSpec
                            .classBuilder(classNameOf(contextName))
                            .addModifiers(Modifier.PUBLIC));

                    var innerTermBuilder = buildersByUnitName.remove(unit.getUnitName());

                    outerContextBuilder.addType(
                        innerTermBuilder
                            .addModifiers(Modifier.STATIC)
                            .build());
                }
            });

            for (var builder : buildersByUnitName.values()) {
                var typeSpec = builder.build();
                JavaFile javaFile = JavaFile.builder(config.getJavaPackage(), typeSpec).build();
                Path outputFile = getFilePath(javaFile, dir);

                StringBuilder code = new StringBuilder();
                javaFile.writeTo(code);
                var formatterStep = GoogleJavaFormatStep.create("1.17.0", "AOSP", new Provisioner() {
                    @Override
                    @Nonnull
                    public Set<File> provisionWithTransitives(boolean withTransitives, Collection<String> mavenCoordinates) {
                        return Stream.of(
                            Formatter.class,
                            RangeSet.class
                        ).map(
                            (Class<?> c) -> {
                                try {
                                    var sourceLocation = c.getProtectionDomain().getCodeSource().getLocation();
                                    return Paths.get(sourceLocation.toURI()).toFile();
                                } catch (URISyntaxException e) {
                                    throw new RuntimeException(e);
                                }
                            }).collect(Collectors.toSet());

                    }
                });

                var formattedCodeString = Objects.requireNonNull(
                    formatterStep.format(code.toString(),
                        NO_FILE_SENTINEL)
                );

                asCharSink(outputFile.toFile(), StandardCharsets.UTF_8).write(formattedCodeString);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (FormatterException fe) {
            throw new RuntimeException(fe);
        } catch (Exception e) {
            throw new UncheckedExecutionException(e);
        }
    }

    private TypeSpec.Builder createTypeSpecBuilder(Unit unit) {
        return (unit instanceof Enumeration)
            ? createEnumBuilder((Enumeration) unit)
            : TypeSpec
            .classBuilder(classNameOf(unit.getName()))
            .addModifiers(Modifier.PUBLIC);
    }

    private TypeSpec.Builder createEnumBuilder(Enumeration enumeration) {
        var typeSpecBuilder = TypeSpec
            .enumBuilder(classNameOf(enumeration.getName()))
            .addModifiers(Modifier.PUBLIC);

        for (var v : enumeration.getValues()) {
            typeSpecBuilder.addEnumConstant(
                v.toUpperCase().replace(" ", "_"),
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

        return typeSpecBuilder;
    }

    private void apply(Relation<?, ?> relation, Map<String, TypeSpec.Builder> buildersByName) {
        var builder = buildersByName.get(relation.getMutant().getUnitName());
        if (relation instanceof Composition) {
            Relations.applyComposition((Composition) relation, builder, language, config);
        } else if (relation instanceof Categorization) {
            Relations.applyCategorization((Categorization) relation, builder, language, config);
        } else if (relation instanceof Description) {
            Relations.applyDescription((Description) relation, builder);
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

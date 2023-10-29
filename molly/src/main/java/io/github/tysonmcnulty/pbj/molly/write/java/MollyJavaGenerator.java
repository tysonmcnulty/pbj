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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.diffplug.spotless.Formatter.NO_FILE_SENTINEL;
import static io.github.tysonmcnulty.pbj.molly.write.java.Syntax.classNameOf;

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

            Map<String, TypeSpec.Builder> buildersByName = unitsToWrite.stream()
                .map((unit) -> Map.entry(unit.getName(), createTypeSpecBuilder(unit)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

            language.getRelations().forEach((relation) -> {
                apply(relation, buildersByName);
            });

            unitsToWrite.forEach(unit -> {
                if (unit.getContext().isPresent()) {
                    var outerContextBuilder = buildersByName.computeIfAbsent(
                        unit.getContext().get(),
                        contextName -> TypeSpec
                            .classBuilder(classNameOf(contextName))
                            .addModifiers(Modifier.PUBLIC));

                    var innerTermBuilder = buildersByName.remove(unit.getName());

                    outerContextBuilder.addType(
                        innerTermBuilder
                            .addModifiers(Modifier.STATIC)
                            .build());

                }
            });

            var categorizationsByMutantName = language.getRelations().stream()
                .filter(r -> r instanceof Categorization)
                .map(r -> (Categorization) r)
                .collect(Collectors.toUnmodifiableMap(c -> c.getMutant().getUnitName(), c -> c));

            var categorizationScore = getUnitNameComparator(categorizationsByMutantName);

            var sortedBuilderEntries = buildersByName.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> categorizationScore.apply(e.getKey())))
                .collect(Collectors.toList());

            Map<String, TypeSpec> typeSpecs = new HashMap<>();
            for (var entry : sortedBuilderEntries) {
                var builder = entry.getValue();
                if (categorizationsByMutantName.containsKey(entry.getKey())) {
                    var parent = categorizationsByMutantName.get(entry.getKey()).getMutation();
                    var parentTypeSpec = typeSpecs.get(parent.getUnitName());
                    if (parentTypeSpec.hasModifier(Modifier.ABSTRACT)) {
                        builder.addModifiers(Modifier.ABSTRACT);
                    }
                }
                typeSpecs.put(entry.getKey(), builder.build());
            }

            for (var typeSpec : typeSpecs.values()) {
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

                var formatted = Objects.requireNonNull(
                    formatterStep.format(code.toString(),
                        NO_FILE_SENTINEL)
                );

                CharSink sink = com.google.common.io.Files.asCharSink(outputFile.toFile(), StandardCharsets.UTF_8);
                System.out.printf("Writing %s to %s%n", typeSpec.name, outputFile.getParent().toString());
                sink.write(formatted);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (FormatterException fe) {
            throw new RuntimeException(fe);
        } catch (Exception e) {
            throw new UncheckedExecutionException(e);
        }
    }

    private static Function<String, Integer> getUnitNameComparator(Map<String, Categorization> categorizationsByMutantName) {
        return (name) -> {
            var score = 0;
            var curr = name;
            do {
                var nextCategorization = categorizationsByMutantName.getOrDefault(curr, null);
                if (nextCategorization == null) {
                    return score;
                }
                score++;
                curr = nextCategorization.getMutation().getName();
            } while (true);
        };
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
        var builder = buildersByName.get(relation.getMutant().getName());
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

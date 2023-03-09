package com.vmware.pbj.molly.write;

import com.google.common.base.Preconditions;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.vmware.pbj.molly.core.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static com.vmware.pbj.molly.write.Syntax.classNameOf;

public class MollyJavaGenerator {

    public MollyJavaGenerator(Language language) {
        this(language, MollyJavaGeneratorConfig.builder().build());
    }

    private final MollyJavaGeneratorConfig config;
    private final Language language;

    private static final Formatter FORMATTER = new Formatter(JavaFormatterOptions.builder()
        .style(JavaFormatterOptions.Style.AOSP)
        .build());

    public MollyJavaGenerator(Language language, MollyJavaGeneratorConfig config) {
        this.language = language;
        this.config = config;
    }

    public void write(Path dir) {
        try {
            Map<String, TypeSpec.Builder> buildersByName = language.getUnits()
                .map((unit) -> {
                    var typeSpecBuilder = TypeSpec
                        .classBuilder(classNameOf(unit.getName()))
                        .addModifiers(Modifier.PUBLIC);

                    return Map.entry(unit.getName(), typeSpecBuilder);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

            language.getRelations().forEach((relation) -> {
                apply(relation, buildersByName);
            });

            language.getUnits().forEach(unit -> {
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

            for (var builder : buildersByName.values()) {
                TypeSpec typeSpec = builder.build();
                JavaFile javaFile = JavaFile.builder(config.getJavaPackage(), typeSpec).build();
                Path outputFile = getFilePath(javaFile, dir);

                StringBuilder code = new StringBuilder();
                javaFile.writeTo(code);

                CharSink sink = com.google.common.io.Files.asCharSink(outputFile.toFile(), StandardCharsets.UTF_8);
                System.out.printf("Writing %s to %s%n", typeSpec.name, dir);
                FORMATTER.formatSource(CharSource.wrap(code), sink);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (FormatterException fe) {

            throw new RuntimeException(fe);
        }
    }

    private void apply(Relation<?, ?> relation, Map<String, TypeSpec.Builder> buildersByName) {
        var builder = buildersByName.get(relation.getMutant().getName());
        if (relation instanceof Composition) {
            Relations.applyComposition((Composition) relation, builder, new Syntax.TypeNameResolver(buildersByName, config));
        } else if (relation instanceof Categorization) {
            Relations.applyCategorization((Categorization) relation, builder, config);
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

package io.github.tysonmcnulty.pbj.molly.write.proto;

import com.google.common.base.Preconditions;
import com.google.protobuf.DescriptorProtos;
import io.github.tysonmcnulty.pbj.molly.core.Language;
import io.github.tysonmcnulty.pbj.molly.core.relation.*;
import io.github.tysonmcnulty.pbj.molly.core.term.Enumeration;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.tysonmcnulty.pbj.molly.write.proto.Syntax.enumValuesOf;
import static io.github.tysonmcnulty.pbj.molly.write.proto.Syntax.messageNameOf;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class MollyProtoGenerator {

    public MollyProtoGenerator(Language language) {
        this(language, MollyProtoGeneratorConfig.builder().build());
    }

    private final MollyProtoGeneratorConfig config;
    private final Language language;

    public MollyProtoGenerator(Language language, MollyProtoGeneratorConfig config) {
        this.language = language;
        this.config = config;
    }

    public void write(Path dir) {
        var fileDescriptorSetBuilder = createFileDescriptorSetBuilder();
        try {
            Files.createDirectories(dir);
            Preconditions.checkArgument(Files.notExists(dir) || Files.isDirectory(dir),
                    "path %s exists but is not a directory.", dir);
            var outputFile = dir.resolve("language.desc");
            fileDescriptorSetBuilder.build().writeTo(Files.newOutputStream(outputFile));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public DescriptorProtos.FileDescriptorSet.Builder createFileDescriptorSetBuilder() {
        var definitionMutants = language.getRelations().stream()
                .filter(r -> r instanceof Definition)
                .map(Relation::getMutant)
                .collect(Collectors.toSet());

        var messageUnits = language.getUnits()
                .filter((unit) -> !(unit instanceof Enumeration))
                .filter((unit) -> !definitionMutants.contains(unit))
                .collect(toList());

        Map<String, DescriptorProtos.DescriptorProto.Builder> messageBuildersByUnitName = messageUnits.stream()
                .collect(toMap(Unit::getUnitName, this::createMessageBuilder, (a, b) -> a));

        var context = new MollyProtoGenerationContext(
                messageBuildersByUnitName,
                language,
                config
        );

        language.getRelations().forEach((relation) -> apply(relation, context));

        messageUnits.forEach(unit -> {
            if (unit.getContext().isPresent()) {
                var outerMessageBuilder = messageBuildersByUnitName.computeIfAbsent(
                        unit.getContext().get(),
                        contextName -> DescriptorProtos.DescriptorProto.newBuilder()
                                .setName(messageNameOf(contextName)));

                var innerMessageBuilder = messageBuildersByUnitName.remove(unit.getUnitName());

                outerMessageBuilder.addNestedType(innerMessageBuilder);
            }
        });

        var enumUnits = language.getUnits()
                .filter((unit) -> unit instanceof Enumeration)
                .map((unit) -> (Enumeration) unit)
                .collect(toList());

        Map<String, DescriptorProtos.EnumDescriptorProto.Builder> enumBuildersByUnitName = enumUnits.stream()
                .collect(toMap(Unit::getUnitName, this::createEnumBuilder, (a, b) -> a));

        language.getUnits().forEach((u) -> {
            Set<Unit> children;
            if (!(children = language.getChildren(u)).isEmpty()) {
                var childrenEnumeration = new Enumeration.Builder(u.getName() + " type")
                        .context(u.getContext().orElse(null))
                        .values(children.stream().map(Unit::getUnitName).toArray(String[]::new))
                        .build();
                apply(new Composition(u, childrenEnumeration), context);
                enumBuildersByUnitName.put(childrenEnumeration.getUnitName(), createEnumBuilder(childrenEnumeration));
            }
        });

        var protoFileDescriptorBuilder = createFileDescriptorBuilder();
        messageBuildersByUnitName.values().forEach(protoFileDescriptorBuilder::addMessageType);
        enumBuildersByUnitName.values().forEach(protoFileDescriptorBuilder::addEnumType);

        return DescriptorProtos.FileDescriptorSet.newBuilder()
                .addFile(protoFileDescriptorBuilder);
    }

    private DescriptorProtos.DescriptorProto.Builder createMessageBuilder(Unit unit) {
        return DescriptorProtos.DescriptorProto.newBuilder().setName(messageNameOf(unit.getName()));
    }

    private DescriptorProtos.EnumDescriptorProto.Builder createEnumBuilder(Enumeration enumeration) {
        DescriptorProtos.EnumDescriptorProto.Builder builder = DescriptorProtos.EnumDescriptorProto.newBuilder()
                .setName(messageNameOf(enumeration.getName()))
                .addValue(DescriptorProtos.EnumValueDescriptorProto.newBuilder()
                        .setName((enumeration.getName() + " unspecified")
                                .toUpperCase()
                                .replaceAll("\\W+", "_"))
                        .setNumber(0)
                        .build());

        var enumValues = enumValuesOf(enumeration);
        for (int i = 0; i < enumValues.size(); i++) {
            builder.addValue(DescriptorProtos.EnumValueDescriptorProto.newBuilder()
                    .setName(enumValues.get(i))
                    .setNumber(i + 1)
                    .build());
        }

        return builder;
    }

    private DescriptorProtos.FileDescriptorProto.Builder createFileDescriptorBuilder() {
        return DescriptorProtos.FileDescriptorProto.newBuilder()
                .setName("model.proto")
                .setPackage("molly")
                .setOptions(DescriptorProtos.FileOptions.newBuilder()
                        .setJavaPackage(config.getJavaPackage() + ".proto"))
                .setEdition("2023")
                .setSyntax("proto3");
    }

    private void apply(Relation<?, ?> relation, MollyProtoGenerationContext context) {
        if (relation instanceof Composition) {
            Relations.applyComposition((Composition) relation, context);
        } else if (relation instanceof Categorization) {
            Relations.applyCategorization((Categorization) relation, context);
        } else if (relation instanceof Description) {
            Relations.applyDescription((Description) relation, context);
        }
    }
}

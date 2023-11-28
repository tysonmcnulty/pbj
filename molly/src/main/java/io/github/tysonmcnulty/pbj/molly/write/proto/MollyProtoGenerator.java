package io.github.tysonmcnulty.pbj.molly.write.proto;

import com.google.common.base.Preconditions;
import com.google.protobuf.DescriptorProtos;
import io.github.tysonmcnulty.pbj.molly.core.Language;
import io.github.tysonmcnulty.pbj.molly.core.relation.*;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.tysonmcnulty.pbj.molly.write.proto.Syntax.messageNameOf;

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

        var unitsToWrite = language.getUnits()
                .filter((unit) -> !definitionMutants.contains(unit))
                .collect(Collectors.toList());

        Map<String, DescriptorProtos.DescriptorProto.Builder> buildersByUnitName = unitsToWrite.stream()
                .collect(Collectors.toMap(Unit::getUnitName, this::createMessageBuilder, (a, b) -> a));

        var context = new MollyProtoGenerationContext(
                buildersByUnitName,
                language,
                config
        );

        language.getRelations().forEach((relation) -> apply(relation, context));

        unitsToWrite.forEach(unit -> {
            if (unit.getContext().isPresent()) {
                var outerMessageBuilder = buildersByUnitName.computeIfAbsent(
                        unit.getContext().get(),
                        contextName -> DescriptorProtos.DescriptorProto.newBuilder()
                                .setName(messageNameOf(contextName)));

                var innerMessageBuilder = buildersByUnitName.remove(unit.getUnitName());

                outerMessageBuilder.addNestedType(innerMessageBuilder);
            }
        });

        var protoFileDescriptorBuilder = createFileDescriptorBuilder();
        buildersByUnitName.values().forEach(protoFileDescriptorBuilder::addMessageType);

        return DescriptorProtos.FileDescriptorSet.newBuilder()
                .addFile(protoFileDescriptorBuilder);
    }

    private DescriptorProtos.DescriptorProto.Builder createMessageBuilder(Unit unit) {
        return DescriptorProtos.DescriptorProto.newBuilder().setName(messageNameOf(unit.getName()));
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

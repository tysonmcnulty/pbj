package io.github.tysonmcnulty.pbj.molly.write.proto;

import com.google.common.base.Preconditions;
import com.google.protobuf.DescriptorProtos;
import io.github.tysonmcnulty.pbj.molly.core.Language;
import io.github.tysonmcnulty.pbj.molly.core.relation.*;
import io.github.tysonmcnulty.pbj.molly.core.term.Term;
import io.github.tysonmcnulty.pbj.molly.core.term.Unit;
import org.jetbrains.annotations.NotNull;

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
        // create collection of message builders for each standalone unit
        var definitionMutants = language.getRelations().stream()
                .filter(r -> r instanceof Definition)
                .map(Relation::getMutant)
                .collect(Collectors.toSet());

        var unitsToWrite = language.getUnits()
                .filter((unit) -> !definitionMutants.contains(unit))
                .collect(Collectors.toList());

        Map<String, DescriptorProtos.DescriptorProto.Builder> buildersByName = unitsToWrite.stream()
                .collect(Collectors.toMap(Term::getName, this::createMessageBuilder, (a, b) -> a));

        language.getRelations().forEach((relation) -> apply(relation, buildersByName));

        // iterate through relations and add specifications to messages corresponding to mutants
        // - categorizations get tricky
        //   - how about fields that appear on the supertype? how do those get preserved in the subtype?
        //     - for protobufs, "there are no supertypes" I guess. We have to cast "up" within the abstract class.
        // - descriptions create bool fields
        // - compositions are simple enough
        //   - what about categorical compositions?
        //     - it's a subtyping issue again. I guess we'll see.
        //     - I think having a "container" will help the protobuf compatibility.

        // create a file descriptor set builder
        var protoFileDescriptorBuilder = createFileDescriptorBuilder();
        buildersByName.values().forEach(protoFileDescriptorBuilder::addMessageType);

        // create set builder and write it out
        var fileDescriptorSetBuilder = DescriptorProtos.FileDescriptorSet.newBuilder()
                .addFile(protoFileDescriptorBuilder);
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

    private DescriptorProtos.DescriptorProto.Builder createMessageBuilder(Unit unit) {
        return DescriptorProtos.DescriptorProto.newBuilder().setName(messageNameOf(unit.getName()));
    }

    @NotNull
    private DescriptorProtos.FileDescriptorProto.Builder createFileDescriptorBuilder() {
        return DescriptorProtos.FileDescriptorProto.newBuilder()
                .setName("model.proto")
                .setPackage("molly")
                .setOptions(DescriptorProtos.FileOptions.newBuilder()
                        .setJavaPackage(config.getJavaPackage() + ".proto"))
                .setEdition("2023")
                .setSyntax("proto3");
    }

    private void apply(Relation<?, ?> relation, Map<String, DescriptorProtos.DescriptorProto.Builder> buildersByName) {
        var builder = buildersByName.get(relation.getMutant().getName());
        if (relation instanceof Composition) {
            Relations.applyComposition((Composition) relation, builder, language, config);
        } else if (relation instanceof Categorization) {
            Relations.applyCategorization((Categorization) relation, builder, language, config);
        } else if (relation instanceof Description) {
            Relations.applyDescription((Description) relation, builder);
        }
    }
}

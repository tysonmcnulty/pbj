package io.github.tysonmcnulty.pbj.molly.write.proto;

import com.google.common.base.Preconditions;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import io.github.tysonmcnulty.pbj.molly.core.Language;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        System.out.println("proto code generation write step");
        System.out.println("language: " + language);
        System.out.println("output base dir: " + dir);
        System.out.println("java package: " + config.getJavaPackage());

        var protoFileDescriptorBuilder = DescriptorProtos.FileDescriptorProto.newBuilder();

        protoFileDescriptorBuilder
                .setName("model.proto")
                .setPackage("molly")
                .setOptions(DescriptorProtos.FileOptions.newBuilder()
                        .setJavaPackage(config.getJavaPackage() + ".proto"))
                .setEdition("2023")
                .setSyntax("proto3")
                .addMessageType(DescriptorProtos.DescriptorProto.newBuilder()
                        .setName("ExampleMessage")
                        .addField(DescriptorProtos.FieldDescriptorProto.newBuilder()
                                .setName("foo")
                                .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING)
                                .setNumber(1))
                        .addField(DescriptorProtos.FieldDescriptorProto.newBuilder()
                                .setName("bar")
                                .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32)
                                .setNumber(2)
                                .setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED)));

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
}

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

        var protoBuilder = DescriptorProtos.FileDescriptorProto.newBuilder();

        protoBuilder
                .setPackage("example")
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

        Preconditions.checkArgument(Files.notExists(dir) || Files.isDirectory(dir),
                "path %s exists but is not a directory.", dir);
        try {
            Files.createDirectories(dir);
            var outputFile = dir.resolve("example.proto");
            var proto = protoBuilder.build();
            var fileDescriptor = Descriptors.FileDescriptor.buildFrom(proto, new Descriptors.FileDescriptor[]{});
            System.out.println("file descriptor: " + fileDescriptor);
            protoBuilder.build().writeTo(Files.newOutputStream(outputFile));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Descriptors.DescriptorValidationException e) {
            throw new RuntimeException(e);
        }
    }
}

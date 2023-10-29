package io.github.tysonmcnulty.pbj.molly.write.proto;

import io.github.tysonmcnulty.pbj.molly.core.Language;

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
        System.out.println("output dir: " + dir);
        System.out.println("java package: " + config.getJavaPackage());
    }
}

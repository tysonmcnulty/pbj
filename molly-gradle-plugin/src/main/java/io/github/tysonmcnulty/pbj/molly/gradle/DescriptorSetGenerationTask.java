package io.github.tysonmcnulty.pbj.molly.gradle;

import io.github.tysonmcnulty.pbj.molly.MollyListenerInterpreter;
import io.github.tysonmcnulty.pbj.molly.write.proto.MollyProtoGenerator;
import io.github.tysonmcnulty.pbj.molly.write.proto.MollyProtoGeneratorConfig;
import org.gradle.api.tasks.TaskAction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;

abstract public class DescriptorSetGenerationTask extends CodeGenerationTask {

    @TaskAction
    public void execute() {
        FileInputStream languageSource;
        try {
            languageSource = new FileInputStream(getInputFile().get().getAsFile());
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
        var language = new MollyListenerInterpreter().read(languageSource);
        var outputPath = getOutputDir().get().getAsFile().toPath();
        new MollyProtoGenerator(language, createConfig()).write(outputPath);
    }

    private MollyProtoGeneratorConfig createConfig() {
        var configBuilder = new MollyProtoGeneratorConfig.Builder();
        if (getJavaPackage().isPresent()) { configBuilder.javaPackage(getJavaPackage().get()); }
        return configBuilder.build();
    }
}

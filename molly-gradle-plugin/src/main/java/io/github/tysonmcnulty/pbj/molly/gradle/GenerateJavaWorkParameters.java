package io.github.tysonmcnulty.pbj.molly.gradle;

import io.github.tysonmcnulty.pbj.molly.write.MollyJavaGeneratorConfig;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkParameters;

public interface GenerateJavaWorkParameters extends WorkParameters {
    Property<MollyJavaGeneratorConfig> getGeneratorConfig();

    RegularFileProperty getInputFile();
    DirectoryProperty getOutputDir();
}

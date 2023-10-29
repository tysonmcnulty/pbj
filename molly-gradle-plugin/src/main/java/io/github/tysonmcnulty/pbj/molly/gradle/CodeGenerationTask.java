package io.github.tysonmcnulty.pbj.molly.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

abstract public class CodeGenerationTask extends DefaultTask {

    @Input
    @Optional
    abstract public Property<String> getJavaPackage();

    @InputFile
    abstract public RegularFileProperty getInputFile();

    @OutputDirectory
    abstract public DirectoryProperty getOutputDir();

}

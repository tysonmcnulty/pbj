package io.github.tysonmcnulty.pbj.molly.gradle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.OutputDirectory;

import java.util.List;

abstract public class ProtoGenerationTask extends Exec {

    @OutputDirectory
    abstract public DirectoryProperty getOutputDir();
}

package io.github.tysonmcnulty.pbj.molly.gradle;

import org.gradle.api.tasks.TaskAction;

abstract public class ProtoCodeGenerationTask extends CodeGenerationTask {

    @TaskAction
    public void execute() {
        System.out.println("proto code generation task");
        System.out.println("input file: " + getInputFile().get());
        System.out.println("output dir: " + getOutputDir().get());
        System.out.println("java package: " + getJavaPackage().get());
    }
}

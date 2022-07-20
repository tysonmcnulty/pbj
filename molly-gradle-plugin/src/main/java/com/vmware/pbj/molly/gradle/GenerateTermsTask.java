package com.vmware.pbj.molly.gradle;

import com.vmware.pbj.molly.MollyJavaGenerator;
import com.vmware.pbj.molly.MollyJavaGeneratorConfig;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

abstract public class GenerateTermsTask extends DefaultTask {

    @Input
    abstract public Property<String> getJavaPackage();

    @InputFile
    abstract public RegularFileProperty getInputFile();

    @OutputDirectory
    abstract public RegularFileProperty getOutputDir();

    @TaskAction
    public void execute() throws FileNotFoundException {
        var generator = new MollyJavaGenerator(new MollyJavaGeneratorConfig.Builder()
                .javaPackage(getJavaPackage().get())
                .build());

        generator.read(new FileInputStream(getInputFile().get().getAsFile()));
        generator.write(getOutputDir().get().getAsFile().toPath());
    }
}

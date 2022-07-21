package com.vmware.pbj.molly.gradle;

import com.vmware.pbj.molly.MollyJavaGenerator;
import com.vmware.pbj.molly.MollyJavaGeneratorConfig;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

abstract public class GenerateTermsTask extends DefaultTask {

    @Input
    @Optional
    abstract public Property<String> getJavaPackage();

    @InputFile
    abstract public RegularFileProperty getInputFile();

    @OutputDirectory
    abstract public RegularFileProperty getOutputDir();

    @TaskAction
    public void execute() throws FileNotFoundException {
        var config = new MollyJavaGeneratorConfig.Builder();

        if (getJavaPackage().isPresent()) { config.javaPackage(getJavaPackage().get()); }

        var generator = new MollyJavaGenerator(config.build());

        generator.read(new FileInputStream(getInputFile().get().getAsFile()));
        generator.write(getOutputDir().get().getAsFile().toPath());
    }
}

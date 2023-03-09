package com.vmware.pbj.molly.gradle;

import com.vmware.pbj.molly.core.Language;
import com.vmware.pbj.molly.MollyListenerInterpreter;
import com.vmware.pbj.molly.write.MollyJavaGenerator;
import com.vmware.pbj.molly.write.MollyJavaGeneratorConfig;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

abstract public class GenerateJavaTask extends DefaultTask {

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

        InputStream languageSource = new FileInputStream(getInputFile().get().getAsFile());
        Language language = new MollyListenerInterpreter().read(languageSource);
        var generator = new MollyJavaGenerator(language, config.build());

        generator.write(getOutputDir().get().getAsFile().toPath());
    }
}

package com.vmware.pbj.molly.gradle;

import com.vmware.pbj.molly.MollyListenerInterpreter;
import com.vmware.pbj.molly.write.MollyJavaGenerator;
import org.gradle.workers.WorkAction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class GenerateJavaWorkAction implements WorkAction<GenerateJavaWorkParameters> {

    @Override
    public void execute() {
        try {
            var outputPath = getParameters().getOutputDir().get().getAsFile().toPath();
            createGenerator().write(outputPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private MollyJavaGenerator createGenerator() throws FileNotFoundException {
        var languageSource = new FileInputStream(getParameters().getInputFile().get().getAsFile());
        var language = new MollyListenerInterpreter().read(languageSource);
        return new MollyJavaGenerator(language, getParameters().getGeneratorConfig().get());
    }
}

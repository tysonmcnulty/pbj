package com.vmware.pbj.molly.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MollyGradlePlugin implements Plugin<Project> {

    public static final Path DEFAULT_OUTPUT_DIR_LOCATION
            = Paths.get("build", "generated", "sources", "molly", "main", "java");
    public static final Path DEFAULT_INPUT_FILE_LOCATION
            = Paths.get("src", "main", "molly", "language.molly");
    public static final String DEFAULT_SOURCE_SET_NAME
            = SourceSet.MAIN_SOURCE_SET_NAME;

    @Override
    public void apply(Project project) {
        var extension = project.getExtensions().create("molly", MollyExtension.class);

        project.getTasks().register("generateTerms", GenerateTermsTask.class, task -> {
            task.getJavaPackage().set(extension.getJavaPackage());

            var inputFileProperty = extension.getInputFile()
                    .convention(project.getLayout().getProjectDirectory().file(
                            DEFAULT_INPUT_FILE_LOCATION.toString()));

            task.getInputFile().set(inputFileProperty);

            var outputDirProperty = extension.getOutputDir()
                    .convention(project.getLayout().getProjectDirectory().file(
                            DEFAULT_OUTPUT_DIR_LOCATION.toString()));

            task.getOutputDir().set(outputDirProperty);

        });

        var sourceSetNameProperty = extension.getSourceSetName()
                .convention(DEFAULT_SOURCE_SET_NAME);

        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            var sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            var mollySourceSet = sourceSets.getByName(sourceSetNameProperty.get());
            mollySourceSet.getJava().srcDir(project.getTasks().getByName("generateTerms"));
        });
    }
}

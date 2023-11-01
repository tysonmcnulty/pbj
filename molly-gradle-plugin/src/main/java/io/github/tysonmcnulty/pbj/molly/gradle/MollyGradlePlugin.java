package io.github.tysonmcnulty.pbj.molly.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Arrays.asList;

public class MollyGradlePlugin implements Plugin<Project> {

    public static final Path DEFAULT_OUTPUT_DIR_LOCATION
            = Paths.get("build", "generated", "sources", "molly");
    public static final Path DEFAULT_INPUT_FILE_LOCATION
            = Paths.get("src", "main", "molly", "Language.molly");
    public static final String DEFAULT_SOURCE_SET_NAME
            = SourceSet.MAIN_SOURCE_SET_NAME;
    public static final String DEFAULT_PROTOBUF_VERSION = "com.google.protobuf:protobuf-java:3.24.0";

    @Override
    public void apply(Project project) {
        var extension = project.getExtensions().create("molly", MollyExtension.class);

        var inputFileProperty = extension.getInputFile()
                .convention(project.getLayout().getProjectDirectory().file(
                        DEFAULT_INPUT_FILE_LOCATION.toString()));

        var outputDirProperty = extension.getOutputDir()
                .convention(project.getLayout().getProjectDirectory().dir(
                        DEFAULT_OUTPUT_DIR_LOCATION.toString()));

        var javaOutputDir = outputDirProperty.dir(
                Paths.get("main", "java").toString());
        var descriptorSetOutputDir = outputDirProperty.dir(
                Paths.get("main", "proto").toString());

        project.getTasks().register("generateJava", JavaGenerationTask.class, task -> {
            task.getJavaPackage().set(extension.getJavaPackage());
            task.getInputFile().set(inputFileProperty);
            task.getOutputDir().set(javaOutputDir);
        });

        project.getTasks().register("generateDescriptorSet", DescriptorSetGenerationTask.class, task -> {
            task.getJavaPackage().set(extension.getJavaPackage());
            task.getInputFile().set(inputFileProperty);
            task.getOutputDir().set(descriptorSetOutputDir);
        });

        project.getTasks().register("generateJavaProto", ProtoGenerationTask.class, task -> {
            task.setExecutable("protoc");
            task.setWorkingDir(project.getProjectDir());
            task.setArgs(asList(
                    "--descriptor_set_in=" + descriptorSetOutputDir.get() + "/language.desc",
                    "--java_out=" + javaOutputDir.get(),
                    "model.proto"
            ));
            task.getOutputDir().set(javaOutputDir);
        });

        var sourceSetNameProperty = extension.getSourceSetName()
                .convention(DEFAULT_SOURCE_SET_NAME);

        project.getDependencies().add("implementation", DEFAULT_PROTOBUF_VERSION);

        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            var sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            var mollySourceSet = sourceSets.getByName(sourceSetNameProperty.get());
            mollySourceSet.getJava()
                    .srcDir(project.getTasks().getByName("generateJava"));
        });
    }
}

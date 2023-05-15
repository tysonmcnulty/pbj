package com.vmware.pbj.molly.gradle;

import com.vmware.pbj.molly.write.MollyJavaGeneratorConfig;
import org.gradle.api.DefaultTask;
import org.gradle.api.JavaVersion;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.internal.jvm.Jvm;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

abstract public class GenerateJavaTask extends DefaultTask {

    private static final List<String> EXTRA_JAVA16_JVM_ARGS = List.of(
        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
    );

    @Inject
    abstract public WorkerExecutor getWorkerExecutor();

    @Input
    @Optional
    abstract public Property<String> getJavaPackage();

    @InputFile
    abstract public RegularFileProperty getInputFile();

    @OutputDirectory
    abstract public DirectoryProperty getOutputDir();

    @TaskAction
    public void execute() {
        var workQueue = getWorkerExecutor().processIsolation(workerSpec -> {
            workerSpec.forkOptions(options -> {
                if (Objects.requireNonNull(Jvm.current().getJavaVersion())
                    .compareTo(JavaVersion.VERSION_16) >= 0
                ) {
                    options.setJvmArgs(EXTRA_JAVA16_JVM_ARGS);
                }
            });
        });

        workQueue.submit(GenerateJavaWorkAction.class, parameters -> {
            parameters.getGeneratorConfig().set(createConfig());
            parameters.getInputFile().set(getInputFile());
            parameters.getOutputDir().set(getOutputDir());
        });

        workQueue.await();
    }

    private MollyJavaGeneratorConfig createConfig() {
        var configBuilder = new MollyJavaGeneratorConfig.Builder();
        if (getJavaPackage().isPresent()) { configBuilder.javaPackage(getJavaPackage().get()); }
        return configBuilder.build();
    }
}

package io.github.tysonmcnulty.pbj.molly.gradle;

import io.github.tysonmcnulty.pbj.molly.write.java.MollyJavaGeneratorConfig;
import org.gradle.api.JavaVersion;
import org.gradle.api.tasks.*;
import org.gradle.internal.jvm.Jvm;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

abstract public class JavaGenerationTask extends CodeGenerationTask {

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

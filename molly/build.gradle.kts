import java.nio.file.Paths as FilePaths

plugins {
    java
    antlr
    idea
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.9.3")
    implementation("org.antlr:antlr4-runtime:4.9.3")
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

val grammarPackageName = "com.vmware.pbj.molly"

sourceSets.create("generated") {
    java.srcDir("generated-src/antlr/main/")
}

tasks.register<JavaExec>("grun") {
    dependsOn(":compileJava")

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.antlr.v4.gui.TestRig")
    maxHeapSize = "128m"
    standardInput = System.`in`
    args = listOf("$grammarPackageName.Chat", "chat")
}

tasks.named<AntlrTask>("generateGrammarSource") {
    maxHeapSize = "128m"
    outputDirectory = file(FilePaths.get(outputDirectory.path, *grammarPackageName.split('.').toTypedArray()))
    arguments = arguments + listOf("-package", grammarPackageName, "-visitor", "-no-listener")
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(":generateGrammarSource")
    source(sourceSets["generated"].java, sourceSets["main"].java)
}

tasks.named("clean") {
    delete("generated-src")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

idea {
    module {
        sourceDirs = sourceDirs + file("generated-src/antlr/main/")
    }
}

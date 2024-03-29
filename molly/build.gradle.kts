import java.nio.file.Paths as FilePaths

plugins {
    java
    antlr
    idea
    `maven-publish`
    id("io.github.tysonmcnulty.pbj.molly") version "0.0.2-SNAPSHOT"
}

val grammarPackageName = "io.github.tysonmcnulty.pbj.molly"
val versionRegex = Regex("""\d+\.\d+\.\d+(?:-SNAPSHOT)?""")

group = "io.github.tysonmcnulty.pbj"
version = (
        if (System.getenv("MOLLY_VERSION") != null)
            versionRegex.find(System.getenv("MOLLY_VERSION"))?.value ?: "dev-SNAPSHOT"
        else
            "dev-SNAPSHOT")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

molly {
    javaPackage = "io.github.tysonmcnulty.pbj.molly.core.model"
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.9.3")

    api("org.slf4j:slf4j-api:2.0.7")

    implementation("org.antlr:antlr4-runtime:4.9.3")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("com.google.googlejavaformat:google-java-format:1.17.0")
    implementation("com.google.protobuf:protobuf-java:3.24.0")
    implementation("com.google.protobuf:protobuf-java-util:3.24.0")
    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.hypertino:inflector_2.13:1.0.13")
    implementation("com.diffplug.spotless:spotless-lib:2.38.0")

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework:spring-core:5.3.21")
}

sourceSets.create("generated") {
    java.srcDir("generated-src/antlr/main/")
}

tasks.register<JavaExec>("grun") {
    dependsOn(":compileJava")

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.antlr.v4.gui.TestRig")
    maxHeapSize = "128m"
    standardInput = System.`in`
    args = listOf("$grammarPackageName.Molly", "file", "-tokens")
}

tasks.named<AntlrTask>("generateGrammarSource") {
    maxHeapSize = "128m"
    outputDirectory = file(FilePaths.get(outputDirectory.path, *grammarPackageName.split('.').toTypedArray()))
    arguments = arguments + listOf("-package", grammarPackageName, "-no-visitor", "-listener")
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(":generateGrammarSource")
    source(sourceSets["generated"].java, sourceSets["main"].java)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

idea {
    module {
        generatedSourceDirs = generatedSourceDirs + file("generated-src/antlr/main/")
    }
}

publishing {
    publications {
        register<MavenPublication>("molly") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tysonmcnulty/pbj")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

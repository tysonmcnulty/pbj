import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("com.vmware.pbj.molly", "molly-gradle-plugin", "0.0.1-SNAPSHOT")
    }
}

plugins {
    id("molly-gradle-plugin")
    idea
    `java-library`
    kotlin("jvm") version "1.6.21"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

molly {
    javaPackage.set("com.vmware.pbj.core.model")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(platform("io.cucumber:cucumber-bom:7.3.0"))
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation(kotlin("test"))

    testImplementation("io.cucumber:cucumber-java")
    testImplementation("io.cucumber:cucumber-junit")
    testImplementation("io.cucumber:cucumber-junit-platform-engine")
    testImplementation("org.junit.platform:junit-platform-suite")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.create("printSourceSetInformation") {
    doLast{
        sourceSets.forEach { srcSet ->
            println("["+srcSet.name+"]")
            print("-->Source directories: "+srcSet.allJava.srcDirs+"\n")
            print("-->Output directories: "+srcSet.output.classesDirs.files+"\n")
            println("")
        }
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("cucumber.junit-platform.naming-strategy", "long")

    testLogging {
        events("passed", "skipped", "failed")
    }
}

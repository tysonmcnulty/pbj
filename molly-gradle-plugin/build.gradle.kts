plugins {
    `java-gradle-plugin`
    `maven-publish`
}

group = "io.github.tysonmcnulty.pbj"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "molly-gradle-plugin"
            implementationClass = "io.github.tysonmcnulty.pbj.molly.gradle.MollyGradlePlugin"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.tysonmcnulty.pbj:molly:0.0.1-SNAPSHOT")
}

publishing {
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

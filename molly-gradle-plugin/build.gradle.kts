plugins {
    `java-gradle-plugin`
    `maven-publish`
}

group = "io.github.tysonmcnulty.pbj"
version = "0.0.2-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

gradlePlugin {
    plugins {
        create("molly") {
            id = "io.github.tysonmcnulty.pbj.molly"
            implementationClass = "io.github.tysonmcnulty.pbj.molly.gradle.MollyGradlePlugin"
        }
    }
}

repositories {
    mavenCentral {
        content {
            excludeModule("io.github.tysonmcnulty.pbj", "molly")
        }
    }
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/tysonmcnulty/pbj")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("io.github.tysonmcnulty.pbj:molly:0.0.7-SNAPSHOT")
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

plugins {
    `java-gradle-plugin`
    `maven-publish`
}

val versionRegex = Regex("""\d+\.\d+\.\d+(?:-SNAPSHOT)?""")

group = "io.github.tysonmcnulty.pbj"
version = (
        if (System.getenv("MOLLY_GRADLE_PLUGIN_VERSION") != null)
            versionRegex.find(System.getenv("MOLLY_GRADLE_PLUGIN_VERSION"))?.value ?: "dev-SNAPSHOT"
        else
            "dev-SNAPSHOT")

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
    implementation("io.github.tysonmcnulty.pbj:molly:0.0.4")
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

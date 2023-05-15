# molly-gradle-plugin

## Installation

1. Add a repository for the Molly Gradle plugin to your settings file:

   ```kotlin
   // settings.gradle.kts
   pluginManagement {
       repositories {
           maven {
               name = "MollyGradlePluginRepository"
               url = uri("https://maven.pkg.github.com/tysonmcnulty/pbj")
               credentials {
                   username = System.getenv("GITHUB_ACTOR")
                   password = System.getenv("GITHUB_TOKEN")
               }
           }
           gradlePluginPortal()
       }
   }
   ```

1. Include and configure the Molly Gradle plugin in your build file:

   ```kotlin
   // build.gradle.kts
   plugins {
     id("molly-gradle-plugin") version "0.0.1-SNAPSHOT"
   }
   
   molly {
     javaPackage.set("com.example")
   }
   ```
   
1. Create a language file at the `src/main/molly/Language.molly` location:

   ```markdown
   - foo
   - bar
   - baz
   ```

## Usage

1. [Authenticate to GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages). Store your credentials in the shell environment where you run Gradle commands:
   
   ```shell
   export GITHUB_ACTOR=<replace with your GitHub username>
   export GITHUB_TOKEN=<replace with your GitHub personal access token>
   ```

1. Run the `generateJava` Gradle task:

   ```shell
   ./gradlew generateJava
   ```

   Your generated files appear in your configured `outputDir` directory (`build/generated/sources/molly` by default).

## Configuration

The plugin accepts the following parameters:

- `inputFile`: the file containing your language definition.
- `javaPackage`: the Java package for your generated source files
- `outputDir`: the root directory where Molly writes your generated source files

The following configuration is equivalent to the default configuration for the plugin:

```kotlin
molly {
    inputFile.set(file("src/main/molly/Language.molly"))
    javaPackage.set("io.github.tysonmcnulty")
    outputDir.set(dir("build/generated/sources/molly/main/java"))
}
```

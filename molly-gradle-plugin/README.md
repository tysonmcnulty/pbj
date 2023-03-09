# molly-gradle-plugin

## Installation

There is no external release artifact yet for this project. To play around with it, you'll need to resolve the `molly-gradle-plugin` dependency locally. 

If you'd like to play around with a preconfigured example, check out the [`feature`](./feature) project.

If you'd like to do it yourself, follow the instructions below to create a sample Gradle-based Java project. The example uses the Gradle Kotlin DSL, a Java package of `com.example`, and a project name of `example`, but you are free to change those details if you choose.

1. Create your project in a subdirectory of the monorepo (i.e., one level above this README).

   ```shell
   export MONOREPO_ROOT=$(git rev-parse --show-toplevel)
   cd $MONOREPO_ROOT
   mkdir example
   cd example
   gradle init \
     --project-name=example \
     --type=java-application \
     --test-framework=junit-jupiter \
     --dsl=kotlin \
     --package=com.example \
     --incubating
   ```

1. Add the following line to the `settings.gradle.kts` in the new project:

   ```
   includeBuild("../molly-gradle-plugin")
   ```

1. Add the following to the generated `app/build.gradle.kts` in the new project:

   ```kotlin
   buildscript {
     dependencies {
       classpath("com.vmware.pbj.molly", "molly-gradle-plugin", "0.0.1-SNAPSHOT")
     }
   }

   plugins {
     id("molly-gradle-plugin")
   }
   
   molly {
     javaPackage.set("com.example")
   }
   ```
   
1. Create a language file. Save it as `app/src/main/molly/Language.molly`:

   ```markdown
   - foo
   - bar
   - baz
   ```
   
1. Run the `generateJava` Gradle task in the new project subdirectory:

   ```shell
   ./gradlew generateJava
   ```

1. View your generated files at `build/generated/sources/molly/com/example`.

## Configuration

plugins {
    `java-gradle-plugin`
}

group = "com.vmware.pbj.molly"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "molly-gradle-plugin"
            implementationClass = "com.vmware.pbj.molly.gradle.MollyGradlePlugin"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.vmware.pbj.molly:molly:0.0.1-SNAPSHOT")
}

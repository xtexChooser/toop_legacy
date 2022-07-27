import toop.build.gradle.rust.RustPlugin

plugins {
    id("java")
}

apply<RustPlugin>()

allprojects {
    group = "toop"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

dependencies {
}

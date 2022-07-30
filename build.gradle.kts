import toop.build.gradle.rust.RustPlugin
import toop.build.gradle.rust.task.CargoXBuildTask

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

tasks.getByName<Jar>("jar") {
    archiveVersion.set(null as String?)
}

tasks.getByName<CargoXBuildTask>("compileRust") {
    dependsOn(tasks.getByName("jar"))
}

package toop.build.gradle.rust.task

import org.gradle.kotlin.dsl.getByType
import toop.build.gradle.rust.RustExtension

open class CargoXBuildTask : CargoTask() {

    init {
        argumentProviders.add { listOf("xbuild") }
        argumentProviders.add { listOf("--target", project.extensions.getByType<RustExtension>().targetPath) }
        inputs.dir(project.file("src/main/rust"))
        inputs.file(project.file("Cargo.toml"))
        outputs.dir(project.file("target"))
    }

}
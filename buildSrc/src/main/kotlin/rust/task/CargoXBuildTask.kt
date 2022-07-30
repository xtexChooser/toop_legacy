package toop.build.gradle.rust.task

import org.gradle.kotlin.dsl.getByType
import toop.build.gradle.rust.RustExtension
import java.io.File

open class CargoXBuildTask : CargoTask() {

    init {
        argumentProviders.add { listOf("xbuild") }
        argumentProviders.add { listOf("--target", project.extensions.getByType<RustExtension>().target) }
        inputs.dir(project.file("src/main/rust"))
        inputs.file(project.file("Cargo.toml"))
        outputs.dir(project.file("target"))
    }

}
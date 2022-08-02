package toop.build.gradle.rust.task

import org.gradle.kotlin.dsl.getByType
import toop.build.gradle.rust.RustExtension

open class CargoXBuildTask : CargoTask() {

    init {
        val extension = project.extensions.getByType<RustExtension>()
        argumentProviders.add { listOf("xbuild") }
        argumentProviders.add { listOf("--target", extension.targetPath) }
        inputs.dir(project.file("src/main/rust"))
        inputs.file(project.file("Cargo.toml"))
        outputs.file(project.file("target/${extension.target}/debug/toop"))
    }

}
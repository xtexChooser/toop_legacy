package toop.build.gradle.rust.task

import org.gradle.kotlin.dsl.getByType
import toop.build.gradle.rust.RustExtension
import java.io.File

open class CargoXBuildTask : CargoTask() {

    init {
        args("xbuild", "--target", project.extensions.getByType<RustExtension>().target)
        inputs.dir(project.projectDir.toPath().resolve("src/main/rust").toFile())
        inputs.file(File(project.projectDir, "Cargo.toml"))
        outputs.dir(File(project.buildDir, "rs"))
    }

}
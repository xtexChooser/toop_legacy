package toop.build.gradle.rust

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import toop.build.gradle.rust.task.CargoXBuildTask

class RustPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create("rust", RustExtension::class)
        val compileRust = target.tasks.create("compileRust", CargoXBuildTask::class)
        target.tasks.maybeCreate("build").dependsOn(compileRust)
    }

}
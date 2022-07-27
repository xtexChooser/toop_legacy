package toop.build.gradle.clang

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import toop.build.gradle.clang.task.ClangTask

class ClangObjectPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val compileC = target.tasks.create("compileC", ClangTask::class)
        target.tasks.maybeCreate("build").dependsOn(compileC)
    }

}
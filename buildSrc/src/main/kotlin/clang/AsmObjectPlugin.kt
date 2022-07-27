package toop.build.gradle.clang

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import toop.build.gradle.clang.task.ClangTask

class AsmObjectPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        val compileAsm = target.tasks.create("compileAsm", ClangTask::class)
        target.tasks.maybeCreate("build").dependsOn(compileAsm)
    }

}
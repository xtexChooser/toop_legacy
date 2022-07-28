package toop.build.gradle.clang

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByName
import toop.build.gradle.clang.task.ClangTask
import toop.build.gradle.clang.task.LdTask
import toop.build.gradle.clang.task.ObjcopyTask

class ObjcopyPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        lateinit var objcopy: ObjcopyTask
        target.afterEvaluate {
            if (target.tasks.findByName("link") != null) {
                objcopy.dependsOn(target.tasks.getByName("link"))
                objcopy.input = target.tasks.getByName<LdTask>("link").output
            } else if (target.tasks.findByName("compileC") != null) {
                objcopy.dependsOn(target.tasks.getByName("compileC"))
                objcopy.input = target.tasks.getByName<ClangTask>("compileC").output
            } else if (target.tasks.findByName("compileAsm") != null) {
                objcopy.dependsOn(target.tasks.getByName("compileAsm"))
                objcopy.input = target.tasks.getByName<ClangTask>("compileAsm").output
            }
        }
        objcopy = target.tasks.create("objcopy", ObjcopyTask::class)
        target.tasks.maybeCreate("build").dependsOn(objcopy)
    }

}
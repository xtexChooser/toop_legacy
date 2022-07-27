package toop.build.gradle.clang

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import toop.build.gradle.clang.task.LdTask

class ObjectLinkingPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        val link = target.tasks.create("link", LdTask::class)
        target.tasks.maybeCreate("build").dependsOn(link)
    }

}
package toop.build.gradle.iso

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import toop.build.gradle.iso.task.XorrisoTask

class IsoPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val mkiso = target.tasks.create("mkiso", XorrisoTask::class)
        target.tasks.maybeCreate("build").dependsOn(mkiso)
    }

}
package toop.build.gradle.rust

import org.gradle.api.Project

open class RustExtension(val project: Project) {

    var target = "x86-toop"
    val targetPath get() = project.rootProject.file("src/main/rstargets/$target.json").absolutePath

}
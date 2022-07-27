package toop.build.gradle.rust.task

import org.gradle.api.tasks.AbstractExecTask

open class CargoTask : AbstractExecTask<CargoTask>(CargoTask::class.java) {

    init {
        executable = "cargo"
    }

}
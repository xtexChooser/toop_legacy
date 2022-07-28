package toop.build.gradle.clang.task

import org.gradle.api.tasks.*
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setValue
import java.io.File

open class ObjcopyTask : AbstractExecTask<ObjcopyTask>(ObjcopyTask::class.java) {

    @InputFiles
    val inputProperty = project.objects.property<File>()

    @get:Internal
    var input by inputProperty

    @OutputFile
    val outputProperty = project.objects.property<File>()

    @get:Internal
    var output by outputProperty

    @Input
    @Optional
    val outputFormatProperty = project.objects.property<String?>().convention(null)

    @get:Internal
    var outputFormat by outputFormatProperty

    @Input
    val stripProperty = project.objects.property<Boolean>().value(true)

    @get:Internal
    var strip by stripProperty

    @Input
    @Optional
    val targetSectionProperty = project.objects.property<String?>().value(null)

    @get:Internal
    var targetSection by targetSectionProperty

    @Internal
    var extraArgs: (() -> Set<String>)? = null

    init {
        executable = "llvm-objcopy"
        project.afterEvaluate {
            inputs.file(input)
            outputs.file(output)
        }
    }

    @TaskAction
    override fun exec() {
        setArgs(emptyList())
        if(outputFormatProperty.isPresent) {
            args("-O", outputFormat)
        }
        if(targetSectionProperty.isPresent) {
            args("-j", targetSection)
        }
        if (strip) {
            args("--strip-all")
        }
        if (extraArgs != null) args(extraArgs!!())
        args(input.absolutePath, output.absolutePath)
        super.exec()
    }

    fun extraArgs(provider: (() -> Set<String>)) {
        extraArgs = provider
    }

}
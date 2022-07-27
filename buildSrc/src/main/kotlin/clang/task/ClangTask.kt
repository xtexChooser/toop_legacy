package toop.build.gradle.clang.task

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setValue
import java.io.File

open class ClangTask : AbstractExecTask<ClangTask>(ClangTask::class.java) {

    @Input
    val nostdlibProperty = project.objects.property<Boolean>().value(true)

    @get:Internal
    var nostdlib by nostdlibProperty

    @Input
    val asmOnlyProperty = project.objects.property<Boolean>().value(true)

    @get:Internal
    var asmOnly by asmOnlyProperty

    @Input
    @Optional
    val targetProperty = project.objects.property<String?>().convention(null)

    @get:Internal
    var target by targetProperty

    @OutputFile
    val outputProperty = project.objects.property<File>()

    @get:Internal
    var output by outputProperty

    @InputFiles
    val source = project.objects.fileCollection()

    @InputFiles
    val includes = project.objects.fileCollection()

    init {
        executable = "clang"
        workingDir = project.file("src")
    }

    @TaskAction
    override fun exec() {
        setArgs(emptyList())
        if (nostdlib) {
            args("-nostdlib", "-nostdinc", "-fno-builtin", "-fno-stack-protector")
        }
        if (targetProperty.isPresent) {
            args("-target", target)
        }
        if (asmOnly) {
            args("-c")
        }
        args("-o", output.absolutePath)
        if (!includes.isEmpty) {
            args("-I", includes.files.map { it.absolutePath }.joinToString(separator = ";"))
        }
        args(source.files.map { it.absolutePath })
        super.exec()
    }

    fun source(configure: ConfigurableFileCollection.() -> Unit) = source.apply(configure)

    fun includes(configure: ConfigurableFileCollection.() -> Unit) = includes.apply(configure)

}
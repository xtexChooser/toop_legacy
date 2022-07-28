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

    private val nostdlibProperty = project.objects.property<Boolean>().value(true)

    @get:Input
    var nostdlib by nostdlibProperty

    private val asmOnlyProperty = project.objects.property<Boolean>().value(true)

    @get:Input
    var asmOnly by asmOnlyProperty

    private val genPICProperty = project.objects.property<Boolean>().value(true)

    @get:Input
    var genPIC by genPICProperty

    private val targetProperty = project.objects.property<String?>().convention(null)

    @get:Input
    @get:Optional
    var target by targetProperty

    private val outputProperty = project.objects.property<File>()
        .convention(project.provider { project.buildDir.resolve(source.singleFile.name + ".o") })

    @get:OutputFile
    var output by outputProperty

    @InputFiles
    val source = project.objects.fileCollection()

    @InputFiles
    val includes = project.objects.fileCollection()

    @Internal
    var extraArgs: (() -> Set<String>)? = null

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
        if (genPIC) {
            args("-fPIC")
        }
        args("-o", output.absolutePath)
        if (!includes.isEmpty) {
            args("-I", includes.files.map { it.absolutePath }.joinToString(separator = ";"))
        }
        if (extraArgs != null) args(extraArgs!!())
        args(source.files.map { it.absolutePath })
        super.exec()
    }

    fun source(configure: ConfigurableFileCollection.() -> Unit) = source.apply(configure)

    fun includes(configure: ConfigurableFileCollection.() -> Unit) = includes.apply(configure)

    fun extraArgs(provider: (() -> Set<String>)) {
        extraArgs = provider
    }

}
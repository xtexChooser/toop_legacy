package toop.build.gradle.clang.task

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
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

    init {
        executable = "clang"
        workingDir = project.file("src")
        argumentProviders.add {
            if (nostdlib)
                listOf("-nostdlib", "-nostdinc", "-fno-builtin", "-fno-stack-protector")
            else emptyList()
        }
        argumentProviders.add { if (targetProperty.isPresent) listOf("-target", target) else emptyList() }
        argumentProviders.add { if (asmOnly) listOf("-c") else emptyList() }
        argumentProviders.add { listOf("-o", output.absolutePath) }
        argumentProviders.add {
            if (!includes.isEmpty) listOf(
                "-I",
                includes.files.map { it.absolutePath }.joinToString(separator = ";")
            ) else emptyList()
        }
        argumentProviders.add { source.files.map { it.absolutePath } }
    }

    fun source(configure: ConfigurableFileCollection.() -> Unit) = source.apply(configure)

    fun includes(configure: ConfigurableFileCollection.() -> Unit) = includes.apply(configure)

}
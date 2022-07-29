package toop.build.gradle.clang.task

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setValue
import java.io.File

open class LdTask : AbstractExecTask<LdTask>(LdTask::class.java) {

    @InputFiles
    val source = project.objects.fileCollection()

    @OutputFile
    val outputProperty = project.objects.property<File>()

    @get:Internal
    var output by outputProperty

    @Input
    @Optional
    val entrySymbolProperty = project.objects.property<String?>().convention(null)

    @get:Internal
    var entrySymbol by entrySymbolProperty

    @Input
    val stripProperty = project.objects.property<Boolean>().value(true)

    @get:Internal
    var strip by stripProperty

    @Input
    @Optional
    val imageBaseProperty = project.objects.property<String?>().convention(null)

    @get:Internal
    var imageBase by imageBaseProperty

    @Input
    @Optional
    val textSectionBaseProperty = project.objects.property<String?>().convention(null)

    @get:Internal
    var textSectionBase by textSectionBaseProperty

    init {
        executable = "ld.lld" + OperatingSystem.current().executableSuffix
        if (project.tasks.findByName("compileC") != null) {
            val task = project.tasks.getByName("compileC")
            dependsOn(task)
            source.from(task)
        }
        if (project.tasks.findByName("compileAsm") != null) {
            val task = project.tasks.getByName("compileAsm")
            dependsOn(task)
            source.from(task)
        }
        argumentProviders.add { if (entrySymbolProperty.isPresent) listOf("--entry", entrySymbol) else emptyList() }
        argumentProviders.add { if (imageBaseProperty.isPresent) listOf("--image-base=$imageBase") else emptyList() }
        argumentProviders.add { if (textSectionBaseProperty.isPresent) listOf("-Ttext=$textSectionBase") else emptyList() }
        argumentProviders.add { if (strip) listOf("-s") else emptyList() }
        argumentProviders.add {
            if (project.file("linker.ld").exists())
                listOf("-T${project.file("linker.ld").absolutePath}") else emptyList()
        }
        argumentProviders.add { listOf("-o", output.absolutePath) }
        argumentProviders.add { source.files.map { it.absolutePath } }
    }

    fun source(configure: ConfigurableFileCollection.() -> Unit) = source.apply(configure)

}
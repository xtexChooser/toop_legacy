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
    val imageBaseProperty = project.objects.property<Int?>().convention(null)

    @get:Internal
    var imageBase by imageBaseProperty

    init {
        executable = "ld.lld" + OperatingSystem.current().executableSuffix
    }

    @TaskAction
    override fun exec() {
        setArgs(emptyList())
        if (entrySymbolProperty.isPresent) {
            args("--entry", entrySymbol)
        }
        if (imageBaseProperty.isPresent) {
            args("--image-base=$imageBase")
        }
        if (strip) {
            args("-s")
        }
        args("-o", output.absolutePath)
        args(source.files.map { it.absolutePath })
        super.exec()
    }

    fun source(configure: ConfigurableFileCollection.() -> Unit) = source.apply(configure)

}
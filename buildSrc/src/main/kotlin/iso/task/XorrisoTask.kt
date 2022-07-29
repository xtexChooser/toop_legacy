package toop.build.gradle.iso.task

import org.gradle.api.Task
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setValue
import java.io.File

open class XorrisoTask : AbstractExecTask<XorrisoTask>(XorrisoTask::class.java) {

    private val outputBaseNameProperty = project.objects.property<String>()
        .convention(project.provider { project.name })

    @get:Input
    var outputBaseName by outputBaseNameProperty

    private val outputProperty = project.objects.property<File>()
        .convention(project.provider { project.buildDir.resolve(outputBaseName + "-" + project.version + ".iso") })

    @get:OutputFile
    var output by outputProperty

    @Internal
    var operations: (OperationsBuilder.() -> Unit)? = null

    @Input
    @Optional
    val volumeIDProperty = project.objects.property<String?>().convention(null)

    @get:Internal
    var volumeID by volumeIDProperty

    @Input
    val enableJolietProperty = project.objects.property<Boolean>().convention(true)

    @get:Internal
    var enableJoliet by enableJolietProperty

    init {
        executable = "xorriso"
        workingDir = project.buildDir
        errorOutput = System.out
        argumentProviders.add { listOf("-outdev", output.absolutePath) }
        argumentProviders.add { listOf("-charset", "utf-8") }
        argumentProviders.add { if (volumeIDProperty.isPresent) listOf("-volid", volumeID) else emptySet() }
        argumentProviders.add { if (enableJoliet) listOf("-joliet", "on") else emptySet() }
        argumentProviders.add {
            if (operations != null) {
                OperationsBuilder().also { operations!!.invoke(it) }.args
            } else emptySet()
        }
    }

    @TaskAction
    override fun exec() {
        if (output.exists()) output.delete()
        super.exec()
    }

    fun operations(provider: OperationsBuilder.() -> Unit) {
        operations = provider
    }

    inner class OperationsBuilder internal constructor() {

        val args = mutableListOf<String>()
        fun args(vararg args: String) = this.args.addAll(args)

        fun update(source: String, to: String) = args("-update", source, to)
        fun update(source: Task, to: String) = update(source.outputs.files.singleFile, to)
        fun update(source: File, to: String) =
            update(source.absoluteFile.toPath().relativize(workingDir.toPath()).toFile().path.replace('\\', '/'), to)

        fun boot(key: String, value: String) = args("-boot_image", "any", "$key=$value")
        fun bootCatalog(path: String) = boot("cat_path", path)
        fun bootInfoTable() = boot("boot_info_table", "on")
        fun bootLoadSize(size: Int) = boot("load_size", (size * 512).toString())
        fun bootFile(path: String) = boot("bin_path", path)
        fun bootNoEmul() = boot("emul_type", "no_emulation")

    }

}
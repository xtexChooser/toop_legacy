import toop.build.gradle.iso.IsoPlugin
import toop.build.gradle.iso.task.XorrisoTask

apply<IsoPlugin>()

val mergeElToritoLoader = tasks.create("mergeElToritoLoader") {
    val loaderObjcopy = evaluationDependsOn(":boot:eltorito").tasks.getByName("objcopy")
    val kernelCompileRs = evaluationDependsOn(":").tasks.getByName("compileRust")
    dependsOn(loaderObjcopy, kernelCompileRs)
    inputs.files(loaderObjcopy.outputs.files, kernelCompileRs.outputs.files)

    val output = buildDir.resolve("merged_eltorito_loader.bin")
    outputs.file(output)
    doLast {
        output.outputStream().use { out ->
            loaderObjcopy.outputs.files.singleFile.inputStream().use { it.copyTo(out) }
            rootProject.file("build/rs/x86-toop/debug/toop").inputStream().use { it.copyTo(out) }
        }
    }
}

val mkiso = tasks.getByName<XorrisoTask>("mkiso") {
    dependsOn(mergeElToritoLoader)
    inputs.files(mergeElToritoLoader.outputs.files)
    operations {
        update(mergeElToritoLoader.outputs.files.singleFile.name, "toop.bin")
        bootCatalog("/boot.cat")
        bootInfoTable()
        bootLoadSize(4)
        bootFile("/toop.bin")
        bootNoEmul()
    }
}

tasks.create<Exec>("runIsoQemu") {
    dependsOn(mkiso)
    inputs.files(mkiso.outputs.files)
    executable("qemu-system-i386")
    argumentProviders.add { listOf("-cdrom", mkiso.outputs.files.singleFile.absolutePath) }
    argumentProviders.add { listOf("-serial", "vc") }
    argumentProviders.add { listOf("-s") }
}

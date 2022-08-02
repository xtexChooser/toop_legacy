/*import toop.build.gradle.iso.IsoPlugin
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
            rootProject.file("build/rs/x86/debug/toop").inputStream().use { it.copyTo(out) }
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
    argumentProviders.add { listOf("-device", "isa-debug-exit,iobase=0xf4,iosize=0x04") }
}*/

tasks.create<Exec>("runMultibootQemu") {
    val multiboot = evaluationDependsOn(":boot:multiboot").tasks.getByName("link")
    val kernel = rootProject.tasks.getByName("compileRust")
    dependsOn(multiboot, kernel)
    inputs.files(multiboot.outputs.files, kernel.outputs.files)
    executable("qemu-system-i386")
    argumentProviders.add { listOf("-kernel", multiboot.outputs.files.singleFile.absolutePath) }
    argumentProviders.add { listOf("-initrd", rootProject.file("target/x86/debug/toop").absolutePath) }
    argumentProviders.add { listOf("-serial", "vc") }
    argumentProviders.add { listOf("-s") }
    argumentProviders.add { listOf("-m", "512M") }
    argumentProviders.add { listOf("-device", "isa-debug-exit,iobase=0xf4,iosize=0x04") }
}

import toop.build.gradle.clang.AsmObjectPlugin
import toop.build.gradle.clang.ClangObjectPlugin
import toop.build.gradle.clang.ObjcopyPlugin
import toop.build.gradle.clang.ObjectLinkingPlugin
import toop.build.gradle.clang.task.ClangTask
import toop.build.gradle.clang.task.LdTask
import toop.build.gradle.clang.task.ObjcopyTask

apply<ClangObjectPlugin>()
apply<AsmObjectPlugin>()
apply<ObjectLinkingPlugin>()
apply<ObjcopyPlugin>()

tasks.getByName<ClangTask>("compileC") {
    target = "i686-pc-unknown-elf"
    source {
        from("src/eltorito.c")
    }
    inputs.dir(file("../src"))
}

tasks.getByName<ClangTask>("compileAsm") {
    target = "i686-pc-unknown-elf"
    source {
        from("src/eltorito.S")
    }
    inputs.dir(file("../src"))
}

tasks.getByName<LdTask>("link") {
    output = buildDir.resolve("eltorito")
    textSectionBase = "0x7c00"
}

tasks.getByName<ObjcopyTask>("objcopy") {
    output = buildDir.resolve("eltorito.bin")
    outputFormat = "binary"
    targetSection = ".text"
}

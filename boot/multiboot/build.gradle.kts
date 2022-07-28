import toop.build.gradle.clang.AsmObjectPlugin
import toop.build.gradle.clang.ClangObjectPlugin
import toop.build.gradle.clang.ObjectLinkingPlugin
import toop.build.gradle.clang.task.ClangTask
import toop.build.gradle.clang.task.LdTask

apply<ClangObjectPlugin>()
apply<AsmObjectPlugin>()
apply<ObjectLinkingPlugin>()

tasks.getByName<ClangTask>("compileC") {
    target = "i686-pc-unknown-elf"
    source {
        from("src/multiboot.c")
    }
}

tasks.getByName<ClangTask>("compileAsm") {
    target = "i686-pc-unknown-elf"
    source {
        from("src/multiboot.S")
    }
}

evaluationDependsOn(":boot:loader")
tasks.getByName<LdTask>("link") {
    output = buildDir.resolve("multiboot")
    source {
        from(tasks.getByName("compileC"))
        from(tasks.getByName("compileAsm"))
        from(project(":boot:loader").tasks.getByName("compileC"))
    }
}

import toop.build.gradle.clang.ClangObjectPlugin
import toop.build.gradle.clang.task.ClangTask

apply<ClangObjectPlugin>()

dependencies {
}

tasks.getByName<ClangTask>("compileC") {
    target = "i686-pc-unknown-elf"
    source {
        from(file("src/loader.c"))
    }
}

import org.gradle.jvm.tasks.Jar
import java.io.File

plugins {
    base
}

val builtinsSrc = fileFrom(rootDir, "core", "builtins", "src")
val builtinsNative = fileFrom(rootDir, "core", "builtins", "native")
// TODO: rewrite dependent projects on using build results instead of the fixed location
val builtinsSerialized = File(rootProject.extra["distDir"].toString(), "builtins")

val builtins by configurations.creating

val clean by tasks.getting {
    doLast {
        delete(builtinsSerialized)
    }
}

val JDK_18: String = rootProject.extra["JDK_18"] as String

val serialize by tasks.creating(Exec::class) {
    val outDir = builtinsSerialized
    val inDirs = arrayOf(builtinsSrc, builtinsNative)
    inDirs.forEach { inputs.dir(it) }
    outputs.dir(outDir)

    commandLine(
        "$JDK_18/bin/java",
        "-Didea.io.use.nio2=true",
        "-cp", rootProject.buildscript.configurations["bootstrapCompilerClasspath"].asPath,
        "org.jetbrains.kotlin.serialization.builtins.RunKt",
        outDir, *inDirs
    )
}

val builtinsJar by task<Jar> {
    dependsOn(serialize)
    from(builtinsSerialized) { include("kotlin/**") }
    baseName = "platform-builtins"
    destinationDir = File(buildDir, "libs")
}


val assemble by tasks.getting {
    dependsOn(serialize)
}

artifacts.add(builtins.name, builtinsJar)

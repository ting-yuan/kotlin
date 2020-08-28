plugins {
    kotlin("jvm")
}

val compile by configurations

dependencies {
    compile(projectTests(":compiler:tests-common")) { isTransitive = false }
}

val jar: Jar by tasks
jar.apply {
    from { compile.filter { it.extension == "jar" }.map { zipTree(it) } }
}

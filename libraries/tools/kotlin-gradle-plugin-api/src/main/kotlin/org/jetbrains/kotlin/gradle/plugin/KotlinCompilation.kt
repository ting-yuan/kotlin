/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import groovy.lang.Closure
import org.gradle.api.Named
import org.gradle.api.attributes.HasAttributes
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.util.ConfigureUtil
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonToolOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import java.io.File

interface KotlinCompilationOutput {
    var resourcesDirProvider: Any
    val resourcesDir: File
    val classesDirs: ConfigurableFileCollection

    val allOutputs: FileCollection
}

interface KotlinCompilation<out T : KotlinCommonToolOptions> : Named, HasAttributes, HasKotlinDependencies {
    val target: KotlinTarget

    val compilationName: String

    val kotlinSourceSets: Set<KotlinSourceSet>

    val defaultSourceSet: KotlinSourceSet

    fun defaultSourceSet(configure: KotlinSourceSet.() -> Unit)
    fun defaultSourceSet(configure: Closure<*>) = defaultSourceSet { ConfigureUtil.configure(configure, this) }

    val compileDependencyConfigurationName: String

    var compileDependencyFiles: FileCollection

    val output: KotlinCompilationOutput

    val platformType get() = target.platformType

    val compileKotlinTaskName: String

    val compileKotlinTask: KotlinCompile<T>

    val kotlinOptions: T

    fun kotlinOptions(configure: T.() -> Unit)
    fun kotlinOptions(configure: Closure<*>) = kotlinOptions { ConfigureUtil.configure(configure, this) }

    val compileAllTaskName: String

    companion object {
        const val MAIN_COMPILATION_NAME = "main"
        const val TEST_COMPILATION_NAME = "test"
    }

    fun source(sourceSet: KotlinSourceSet)

    override fun getName(): String = compilationName

    override val relatedConfigurationNames: List<String>
        get() = super.relatedConfigurationNames + compileDependencyConfigurationName
}

interface KotlinCompilationToRunnableFiles<T : KotlinCommonToolOptions> : KotlinCompilation<T> {
    val runtimeDependencyConfigurationName: String

    var runtimeDependencyFiles: FileCollection

    override val relatedConfigurationNames: List<String>
        get() = super.relatedConfigurationNames + runtimeDependencyConfigurationName
}

interface KotlinCompilationWithResources<T : KotlinCommonToolOptions> : KotlinCompilation<T> {
    val processResourcesTaskName: String
}
/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon.common.experimental

import java.io.File

enum class OSKind {
    Windows,
    OSX,
    Unix,
    Unknown;

    companion object {
        val current: OSKind = System.getProperty("os.name").toLowerCase().let {
            when {
                // partly taken from http://www.code4copy.com/java/post/detecting-os-type-in-java
                it.startsWith("windows") -> Windows
                it.startsWith("mac os") -> OSX
                it.contains("unix") -> Unix
                it.startsWith("linux") -> Unix
                it.contains("bsd") -> Unix
                it.startsWith("irix") -> Unix
                it.startsWith("mpe/ix") -> Unix
                it.startsWith("aix") -> Unix
                it.startsWith("hp-ux") -> Unix
                it.startsWith("sunos") -> Unix
                it.startsWith("sun os") -> Unix
                it.startsWith("solaris") -> Unix
                else -> Unknown
            }
        }
    }
}

private fun String?.orDefault(v: String): String =
    if (this == null || this.isBlank()) v else this

// Note links to OS recommendations for storing various kinds of files
// Windows: http://www.microsoft.com/security/portal/mmpc/shared/variables.aspx
// unix (freedesktop): http://standards.freedesktop.org/basedir-spec/basedir-spec-latest.html
// OS X: https://developer.apple.com/library/mac/documentation/FileManagement/Conceptual/FileSystemProgrammingGuide/AccessingFilesandDirectories/AccessingFilesandDirectories.html

object FileSystem {

    val userHomePath: String get() = System.getProperty("user.home")
    val tempPath: String get() = System.getProperty("java.io.tmpdir")

    val logFilesPath: String get() = tempPath

    val runtimeStateFilesBasePath: String get() = when (OSKind.current) {
        OSKind.Windows -> System.getenv("LOCALAPPDATA").orDefault(tempPath)
        OSKind.OSX -> userHomePath + "/Library/Application Support"
        OSKind.Unix -> System.getenv("XDG_DATA_HOME").orDefault(userHomePath + "/.local/share")
        OSKind.Unknown -> tempPath
    }

    fun getRuntimeStateFilesPath(vararg names: String): String {
        assert(names.any())
        val base = File(runtimeStateFilesBasePath)
        // if base is not suitable, take home dir as a base and ensure the first name is prefixed with "." -
        //   this will work ok as a fallback solution on most systems
        val dir = if (base.exists() && base.isDirectory) names.fold(base, ::File)
                  else names.drop(1)
                            .fold(File(userHomePath, names.first().let { if (it.startsWith(".")) it else ".$it" }), ::File)
        return if ((dir.exists() && dir.isDirectory) || dir.mkdirs()) dir.absolutePath
               else tempPath
    }
}


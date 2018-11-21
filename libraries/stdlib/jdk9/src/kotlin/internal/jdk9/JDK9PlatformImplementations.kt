/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER", "CANNOT_OVERRIDE_INVISIBLE_MEMBER")
package kotlin.internal.jdk9

import kotlin.internal.jdk8.JDK8PlatformImplementations

internal open class JDK9PlatformImplementations : JDK8PlatformImplementations() {
    override fun moduleName(clazz: Class<*>): String? = clazz.module.descriptor.name()
}
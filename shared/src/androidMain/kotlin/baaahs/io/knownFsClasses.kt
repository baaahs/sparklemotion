package baaahs.io

import kotlin.reflect.KClass

actual val platformFsClasses: Set<KClass<out Fs>>
    get() = setOf(RealFs::class)
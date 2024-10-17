package baaahs.io

import baaahs.sim.BrowserSandboxFs
import kotlin.reflect.KClass

actual val platformFsClasses: Set<KClass<out Fs>>
    get() = setOf(BrowserSandboxFs::class)
package baaahs

import baaahs.util.Clock
import baaahs.util.SystemClock
import kotlinx.coroutines.runBlocking
import java.util.*

actual fun doRunBlocking(block: suspend () -> Unit) = runBlocking { block() }

actual fun getResource(name: String): String {
    return Pinky::class.java.classLoader.getResource(name).readText()
}

actual val internalTimerClock: Clock = SystemClock

actual fun decodeBase64(s: String): ByteArray = Base64.getDecoder().decode(s)
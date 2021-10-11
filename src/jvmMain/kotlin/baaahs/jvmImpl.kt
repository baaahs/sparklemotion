package baaahs

import baaahs.util.Clock
import baaahs.util.SystemClock
import kotlinx.coroutines.runBlocking
import java.util.*

actual fun doRunBlocking(block: suspend () -> Unit) = runBlocking { block() }

actual val internalTimerClock: Clock = SystemClock

actual fun decodeBase64(s: String): ByteArray = Base64.getDecoder().decode(s)
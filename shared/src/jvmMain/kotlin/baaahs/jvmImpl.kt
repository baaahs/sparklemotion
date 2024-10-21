package baaahs

import baaahs.util.Clock
import baaahs.util.SystemClock
import kotlinx.coroutines.runBlocking
import java.util.*

actual fun <T> doRunBlocking(block: suspend () -> T): T = runBlocking { block() }

actual val internalTimerClock: Clock = SystemClock

actual fun decodeBase64(s: String): ByteArray = Base64.getMimeDecoder().decode(s)

actual fun encodeBase64(b: ByteArray): String = Base64.getMimeEncoder().encodeToString(b)
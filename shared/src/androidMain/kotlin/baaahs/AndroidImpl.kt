package baaahs

import baaahs.util.Clock
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant

actual fun <T> doRunBlocking(block: suspend () -> T): T = runBlocking { block() }

actual val internalTimerClock: Clock = SystemClock

actual fun decodeBase64(s: String): ByteArray = TODO() // Base64.getMimeDecoder().decode(s)

actual fun encodeBase64(b: ByteArray): String = TODO() // Base64.getMimeEncoder().encodeToString(b)

object SystemClock : Clock {
    override fun now(): Instant {
        return kotlinx.datetime.Clock.System.now()
    }
}
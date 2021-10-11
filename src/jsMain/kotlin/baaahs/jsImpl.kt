package baaahs

import baaahs.util.Clock
import baaahs.util.JsClock
import baaahs.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.w3c.dom.get

actual fun doRunBlocking(block: suspend () -> Unit) {
    GlobalScope.promise { block() }
        .catch { t ->
            Logger("doRunBlocking").error(t) { "Error during doRunBlocking()" }
        }
}

val resourcesBase = document["resourcesBase"]

actual val internalTimerClock: Clock = JsClock

actual fun decodeBase64(s: String): ByteArray {
    return window.atob(s).encodeToByteArray()
}

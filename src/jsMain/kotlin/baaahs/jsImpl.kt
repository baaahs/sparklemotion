package baaahs

import baaahs.util.Clock
import baaahs.util.JsClock
import baaahs.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.w3c.dom.get

actual fun <T> doRunBlocking(block: suspend () -> T): T {
    var value: T? = null
    GlobalScope.promise {
        value = block()
    }
        .catch { t ->
            Logger("doRunBlocking").error(t) { "Error during doRunBlocking()" }
        }
    return value!!
}

val resourcesBase: String get() = document["resourcesBase"]

actual val internalTimerClock: Clock = JsClock

actual fun decodeBase64(s: String): ByteArray =
    window.atob(s).let { binaryStr ->
        ByteArray(binaryStr.length) { i ->
            binaryStr.asDynamic().charCodeAt(i)
        }.also { console.log(it) }
    }

// TODO: this is probably busted, fix!
actual fun encodeBase64(b: ByteArray): String =
    window.btoa(b.decodeToString())

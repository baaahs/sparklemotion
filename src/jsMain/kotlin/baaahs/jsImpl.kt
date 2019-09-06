package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date

actual fun doRunBlocking(block: suspend () -> Unit) {
    GlobalScope.promise { block() }
    return
}

actual fun getResource(name: String): String {
    val xhr = XMLHttpRequest()
    xhr.open("GET", name, false)
    xhr.send()

    if (xhr.status.equals(200)) {
        return xhr.responseText
    }

    throw Exception("failed to load resource ${name}: ${xhr.status} ${xhr.responseText}")
}

actual fun getTimeMillis(): Long = Date.now().toLong()

actual fun decodeBase64(s: String): ByteArray {
    TODO("decodeBase64 not implemented")
}

actual fun logMessage(level: String, message: String, exception: Exception?) {
    when (level) {
        "ERROR" -> console.error(message, exception)
        "WARN" -> console.warn(message, exception)
        "INFO" -> console.info(message, exception)
        "DEBUG" -> console.log(message, exception)
        else -> console.log(message, exception)
    }
}
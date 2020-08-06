package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.w3c.dom.get
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

actual fun doRunBlocking(block: suspend () -> Unit) {
    GlobalScope.promise { block() }
    return
}

val resourcesBase = document["resourcesBase"]

actual fun getResource(name: String): String {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "$resourcesBase/$name", false)
    xhr.send()

    if (xhr.status.toInt() == 200) {
        return xhr.responseText
    }

    throw Exception("failed to load resource ${name}: ${xhr.status} ${xhr.responseText}")
}

actual fun getTimeMillis(): Long = Date.now().toLong()

actual fun decodeBase64(s: String): ByteArray {
    return window.atob(s).encodeToByteArray()
}

actual fun log(id: String, level: String, message: String, exception: Throwable?) {
    try {
        logMessage(level, "${Logger.ts()} [] $level  $id - $message", exception)
    } catch (t: Throwable) {
        println("!!! Logger bailing")
    }
}

private fun logMessage(level: String, message: String, exception: Throwable?) {
    when (level) {
        "ERROR" -> console.error(message, exception)
        "WARN" -> console.warn(message, exception)
        "INFO" -> console.info(message, exception)
        "DEBUG" -> console.asDynamic().debug(message, exception)
        else -> console.log(message, exception)
    }
}

actual fun logGroupBegin(id: String, message: String) {
    console.asDynamic().group("$id - $message")
}

actual fun logGroupEnd(id: String, message: String) {
    console.asDynamic().groupEnd()
}

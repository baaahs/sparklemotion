package baaahs.io

import baaahs.resourcesBase
import org.w3c.xhr.XMLHttpRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual fun getResource(name: String): String {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "$resourcesBase/$name", false)
    xhr.send()

    if (xhr.status.toInt() == 200) {
        return xhr.responseText
    }

    throw Exception("failed to load resource ${name}: ${xhr.status} ${xhr.responseText}")
}

actual suspend fun getResourceAsync(name: String): String = suspendCoroutine { c ->
    val xhr = XMLHttpRequest()
    val url = "$resourcesBase/$name"

    xhr.onreadystatechange = {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            if (xhr.status / 100 == 2) {
                c.resume(xhr.responseText)
            } else {
                c.resumeWithException(Exception("HTTP error: GET $url -> $ ${xhr.status}"))
            }
        }
        null
    }

    xhr.open("GET", url, true)
    println("GET $url ...")
    xhr.send()
}

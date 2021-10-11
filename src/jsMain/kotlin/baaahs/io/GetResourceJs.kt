package baaahs.io

import baaahs.resourcesBase
import org.w3c.xhr.XMLHttpRequest

actual fun getResource(name: String): String {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "$resourcesBase/$name", false)
    xhr.send()

    if (xhr.status.toInt() == 200) {
        return xhr.responseText
    }

    throw Exception("failed to load resource ${name}: ${xhr.status} ${xhr.responseText}")
}
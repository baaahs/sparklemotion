package baaahs.util

import baaahs.net.BrowserNetwork
import baaahs.proto.Ports
import baaahs.window
import org.w3c.dom.Location

external fun encodeURIComponent(uri: String): String
external fun decodeURIComponent(encodedURI: String): String

object JsPlatform {
    val myAddress = BrowserNetwork.BrowserAddress(hostname())
    val network by lazy { BrowserNetwork() }
    val networkWithUdpProxy by lazy { BrowserNetwork(myAddress, Ports.PINKY) }

    fun decodeQueryParams(location: Location): Map<String, String> {
        val query = location.search
        if (query.startsWith("?")) {
            return query.substring(1).decodeQueryParams()
        } else {
            return emptyMap()
        }
    }

    fun decodeHashParams(location: Location): Map<String, String> {
        val hash = location.hash
        if (hash.startsWith("#")) {
            return hash.substring(1).decodeQueryParams()
        } else {
            return emptyMap()
        }
    }

    private fun String.decodeQueryParams(): Map<String, String> {
        return replace('+', ' ').split("&").map {
            val (k, v) = it.split("=", limit = 2)
            decodeURIComponent(k) to decodeURIComponent(v)
        }.toMap()
    }

    private fun hostname(): String {
        return window.location.hostname
    }

    private fun websocketsUrl(): String {
        val l = window.location
        val proto = if (l.protocol === "https:") "wss:" else "ws:"
        return "$proto//${l.hostname}/"
    }
}
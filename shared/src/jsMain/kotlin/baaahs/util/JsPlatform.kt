package baaahs.util

import baaahs.net.BrowserNetwork
import org.w3c.dom.Location
import web.location.location

external fun encodeURIComponent(uri: String): String
external fun decodeURIComponent(encodedURI: String): String

object JsPlatform {
    val container: SparkleMotionContainer?
        get() = js("window.SparkleMotionContainer")

    val isBrowser: Boolean
        get() = container == null

    val isNative: Boolean
        get() = container != null

    val isAndoid: Boolean
        get() = container?.platform == "Android"

    val isIos: Boolean
        get() = container?.platform == "iOS"

    val myAddress by lazy {
        if (location.protocol == "file:") {
            container?.urlBase?.let { urlBase ->
                BrowserNetwork.BrowserAddress(urlBase.protocol, urlBase.hostname, urlBase.port.toString())
            } ?: throw IllegalStateException("SparkleMotion cannot be run from a file:// URL. Please run it from a web server.")
        } else
            with(location) { BrowserNetwork.BrowserAddress(protocol, hostname, port) }
    }

    fun imageUrl(path: String): String =
        container?.urlBase?.let { urlBase ->
            with(urlBase) { "$protocol://$hostname:$port$path" }
        } ?: path

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
        return replace('+', ' ').split("&").associate {
            val (k, v) = it.split("=", limit = 2)
            decodeURIComponent(k) to decodeURIComponent(v)
        }
    }
}
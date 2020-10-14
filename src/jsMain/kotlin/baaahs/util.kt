import org.w3c.dom.Location

external fun encodeURIComponent(uri: String): String
external fun decodeURIComponent(encodedURI: String): String


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

fun String.decodeQueryParams(): Map<String, String> {
    return replace('+', ' ').split("&").map {
        val (k, v) = it.split("=", limit = 2)
        decodeURIComponent(k) to decodeURIComponent(v)
    }.toMap()
}

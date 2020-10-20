package baaahs.util

import kotlin.browser.window

object ConsoleFormatters {
    fun install() {
        window.asDynamic().devtoolsFormatters = arrayOf(
            map, set, list
        )
    }

    val map = object : Formatter {
        override fun header(x: Any): dynamic {
            return if (x is Map<*, *>) {
                return jsonMl(
                    "span",
                    "Map with ${x.size} entries; keys: ",
                    x.keys.toList().maxOf(3)
                )
            } else null
        }

        override fun hasBody(x: Any): Boolean = x is Map<*, *> && x.size > 0

        override fun body(x: Any): dynamic {
            x as Map<*, *>

            return jsonMl(
                "table",
                jsonMl(
                    "tr",
                    jsonMl("td", bold, "Key"),
                    jsonMl("td", bold, "Value"),
                ),

                *x.map { (k, v) ->
                    jsonMl(
                        "tr",
                        jsonMl("td", k),
                        jsonMl("td", v),
                    )
                }.toTypedArray()
            )
        }
    }

    val set = object : Formatter {
        override fun header(x: Any): dynamic {
            return if (x is Set<*>) {
                jsonMl("span", "Set with ${x.size} items: ${x.toList().maxOf(3)}")
            } else null
        }

        override fun hasBody(x: Any): Boolean = x is Set<*> && x.size > 0

        override fun body(x: Any): dynamic {
            x as Set<*>

            return jsonMl(
                "table",
                *x.map {
                    jsonMl("tr", jsonMl("td", it))
                }.toTypedArray()
            )
        }
    }

    val list = object : Formatter {
        override fun header(x: Any): dynamic {
            return if (x is List<*>) {
                jsonMl("span", "List with ${x.size} items: ${x.maxOf(3)}")
            } else null
        }

        override fun hasBody(x: Any): Boolean = x is List<*> && x.size > 0

        override fun body(x: Any): dynamic {
            x as List<*>

            return jsonMl(
                "table",
                *x.map {
                    jsonMl("tr", jsonMl("td", it))
                }.toTypedArray()
            )
        }
    }

    interface Formatter {
        // Returns http://www.jsonml.org/ or null.
        @JsName("header")
        fun header(x: Any): dynamic

        @JsName("hasBody")
        fun hasBody(x: Any): Boolean

        // Returns http://www.jsonml.org/ or null.
        @JsName("body")
        fun body(x: Any): dynamic
    }


    private val bold = mapOf("style" to "font-weight: bold")

    private fun <T> List<T>.maxOf(size: Int): Array<dynamic> {
        return if (this.size > size) {
            jsonMl("span",
                *subList(0, 3)
                    .flatMap { listOf(it.asDynamic(), ", ".asDynamic()) }
                    .toTypedArray(),
                "...",
            )
        } else {
            jsonMl("span",
                *flatMap { listOf(it.asDynamic(), ", ".asDynamic()) }
                    .toTypedArray()
            )
        }
    }

    private fun jsonMl(tag: String, vararg children: dynamic): Array<Any> {
        return arrayOf(tag, *children)
    }

    private fun jsonMl(
        tag: String,
        attrs: Map<String, String> = emptyMap(),
        vararg children: dynamic
    ): Array<dynamic> {
        val attrsObj = js("{}")
        attrs.forEach { (k, v) -> attrsObj[k] = v }
        return arrayOf(tag, attrsObj, *children)
    }
}
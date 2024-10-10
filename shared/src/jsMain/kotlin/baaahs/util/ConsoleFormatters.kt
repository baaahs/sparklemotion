@file:Suppress("ObsoleteKotlinJsPackages")

package baaahs.util

import baaahs.window

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
                    noAttrs,
                    "Map with ${x.size} entries; keys: ",
                    *x.keys.toList().maxOf(3)
                )
            } else null
        }

        override fun hasBody(x: Any): Boolean = x is Map<*, *> && x.size > 0

        override fun body(x: Any): dynamic {
            x as Map<*, *>

            return jsonMl(
                "table",
                noAttrs,
                jsonMl(
                    "tr",
                    noAttrs,
                    jsonMl("td", bold, "Key"),
                    jsonMl("td", bold, "Value"),
                ),

                *x.map { (k, v) ->
                    jsonMl(
                        "tr",
                        noAttrs,
                        jsonMl("td", noAttrs, k.asMl()),
                        jsonMl("td", noAttrs, v.asMl()),
                    )
                }.toTypedArray()
            )
        }
    }

    val set = object : Formatter {
        override fun header(x: Any): dynamic {
            return if (x is Set<*>) {
                jsonMl("span", noAttrs, "Set with ${x.size} items:", *x.toList().maxOf(3))
            } else null
        }

        override fun hasBody(x: Any): Boolean = x is Set<*> && x.size > 0

        override fun body(x: Any): dynamic {
            x as Set<*>

            return jsonMl(
                "table",
                noAttrs,
                *x.map {
                    jsonMl("tr", noAttrs, jsonMl("td", noAttrs, it.asMl()))
                }.toTypedArray()
            )
        }
    }

    val list = object : Formatter {
        override fun header(x: Any): dynamic {
            return if (x is List<*>) {
                jsonMl("span", noAttrs, "List with ${x.size} items:", *x.maxOf(3))
            } else null
        }

        override fun hasBody(x: Any): Boolean = x is List<*> && x.size > 0

        override fun body(x: Any): dynamic {
            x as List<*>

            return jsonMl(
                "table",
                noAttrs,
                *x.map {
                    jsonMl("tr", noAttrs, jsonMl("td", noAttrs, it.asMl()))
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
        return when {
            this.isEmpty() -> return emptyArray()
            this.size > size -> {
                subList(0, size)
                    .flatMap { listOf(it.asMl(), ", ") }
                    .toTypedArray() + "..."
            }
            else -> {
                val items = flatMap { listOf(it.asMl(), ", ") }.toMutableList()
                items.removeAt(items.size - 1)
                items.toTypedArray()
            }
        }
    }

    private fun jsonMl(
        tag: String,
        attrs: Map<String, String> = noAttrs,
        vararg children: dynamic
    ): Array<dynamic> {
        return arrayOf(tag, attrs(attrs), *children)
    }

    private fun attrs(attrs: Map<String, Any?>): dynamic {
        val attrsObj = js("{}")
        attrs.forEach { (k, v) -> attrsObj[k] = v }
        return attrsObj
    }

    private fun Any?.asMl(): dynamic {
        return if (this is String || this is Number) {
            this
        } else if (this == null || this == undefined) {
            return this
        } else {
            arrayOf("object", attrs(mapOf("object" to this)))
        }
    }

    private val noAttrs = emptyMap<String, String>()
}
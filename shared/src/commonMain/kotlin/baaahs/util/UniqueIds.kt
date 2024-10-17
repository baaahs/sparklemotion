package baaahs.util

import baaahs.unknown

class UniqueIds<T> {
    private val toId = mutableMapOf<T, String>()
    private val byId = mutableMapOf<String, T>()

    fun all() = byId.toMap()

    operator fun get(id: String): T? = byId[id]

    fun getBang(id: String, type: String): T {
        return byId.get(id) ?: error(unknown(type, id, byId.keys))
    }

    fun idFor(value: T, suggest: () -> String): String {
        return toId.getOrPut(value) {
            val suggestedId = suggest()
            byId.putWithUniqueId(suggestedId, value)
        }
    }

    fun remove(value: T): Boolean {
        val id = toId.remove(value) ?: return false
        byId.remove(id)
        return true
    }

    fun removeId(id: String): Boolean {
        val value = byId.remove(id) ?: return false
        toId.remove(value)
        return true
    }

    fun clear() {
        toId.clear()
        byId.clear()
    }

    fun putAll(items: Map<String, T>) {
        items.forEach { (k, v) -> toId[v] = k }
        byId.putAll(items)
    }
}

fun <V> MutableMap<String, V>.putWithUniqueId(prefix: String, value: V): String {
    val key = if (!containsKey(prefix)) {
        prefix
    } else {
        var i = 2
        while (containsKey("${prefix}$i")) i++
        "${prefix}$i"
    }
    put(key, value)
    return key
}
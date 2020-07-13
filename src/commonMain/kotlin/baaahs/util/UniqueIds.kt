package baaahs.util

class UniqueIds<T> {
    private val toId = mutableMapOf<T, String>()
    private val byId = mutableMapOf<String, T>()

    fun all() = byId.toMap()

    operator fun get(id: String): T? = byId[id]

    fun idFor(value: T, suggest: () -> String): String {
        return toId.getOrPut(value) {
            val suggestedId = suggest()
            byId.putWithUniqueId(suggestedId, value)
        }
    }

    fun remove(value: T): Boolean {
        val id = toId[value] ?: return false
        toId.remove(value)
        byId.remove(id)
        return true
    }
    fun clear() {
        toId.clear()
        byId.clear()
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
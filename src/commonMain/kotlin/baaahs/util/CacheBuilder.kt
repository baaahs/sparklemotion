package baaahs.util

class CacheBuilder<K, V>(val createFn: (K) -> V) {
    private val map = mutableMapOf<K, V>()

    val all: Map<K, V> get() = map

    operator fun get(key: K): V {
        return map.getOrPut(key) { createFn(key) }
    }
}

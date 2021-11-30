package baaahs.util

import baaahs.unknown

class CacheBuilder<K, V>(val createFn: (K) -> V) {
    private val map = mutableMapOf<K, V>()

    val all: Map<K, V> get() = map
    val keys: Collection<K> get() = map.keys
    val values: Collection<V> get() = map.values

    operator fun get(key: K): V {
        return map.getOrPut(key) { createFn(key) }
    }

    fun getBang(key: K, type: String): V = get(key) ?: error(unknown(type, key, map.keys))
}
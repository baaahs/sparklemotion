package baaahs.util

object Delta {
    fun <K, V> diff(oldMap: Map<K, V>, newMap: Map<K, V>, listener: MapChangeListener<K, V>) {
        val toRemove = oldMap.keys - newMap.keys
        val toChange = oldMap.keys.intersect(newMap.keys)
        val toAdd = newMap.keys - oldMap.keys

        for (k in toRemove) {
            listener.onRemove(k, oldMap[k]!!)
        }

        for (k in toChange) {
            val oldValue = oldMap[k]!!
            val newValue = newMap[k]!!
            if (oldValue != newValue) {
                listener.onChange(k, oldValue, newValue)
            }
        }

        for (k in toAdd) {
            listener.onAdd(k, newMap[k]!!)
        }
    }

    interface MapChangeListener<K, V> {
        fun onAdd(key: K, value: V)

        fun onChange(key: K, oldValue: V, newValue: V) {
            onRemove(key, oldValue)
            onAdd(key, newValue)
        }

        fun onRemove(key: K, value: V)
    }
}
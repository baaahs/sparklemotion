package baaahs.util

import baaahs.ui.IObservable
import baaahs.ui.Observable

class Monitor<T : Any?>(
    initialValue: T,
    private val observable: Observable = Observable()
) : IObservable by observable {
    private val beforeChangeListeners = arrayListOf<(T) -> Unit>()

    var value: T = initialValue
        private set

    fun addBeforeChangeListener(callback: (T) -> Unit) {
        beforeChangeListeners.add(callback)
    }

    fun removeBeforeChangeListener(callback: (T) -> Unit) {
        beforeChangeListeners.remove(callback)
    }

    fun onChange(value: T) {
        beforeChangeListeners.forEach { it.invoke(value) }

        this.value = value
        observable.notifyChanged()
    }
}
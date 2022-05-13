package baaahs.util

import baaahs.ui.IObservable
import baaahs.ui.Observable

class Monitor<T : Any?>(
    initialValue: T,
    private val observable: Observable = Observable()
) : IObservable by observable {
    private val beforeChangeListeners = arrayListOf<Listener<T>>()

    var value: T = initialValue
        set(value) {
            beforeChangeListeners.forEach { it.updated(value) }

            field = value
            observable.notifyChanged()
        }

    fun addBeforeChangeListener(callback: Listener<T>) {
        beforeChangeListeners.add(callback)
    }
}

fun interface Listener<T : Any?> {
    fun updated(value: T)
}


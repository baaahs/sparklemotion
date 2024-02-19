package baaahs.ui

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface IObservable {
    fun addObserver(observer: Observer): Observer
    fun removeObserver(observer: Observer)
}

open class Observable : IObservable {
    private val observers = mutableListOf<Observer>()

    protected fun anyObservers(): Boolean = observers.isNotEmpty()

    override fun addObserver(observer: Observer): Observer {
        observers.add(observer)
        return observer
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    fun notifyChanged() {
        observers.forEach { it.notifyChanged() }
    }

    protected fun <V> notifyOnChange(initialValue: V): ReadWriteProperty<Observable, V> {
        return NotifyOnChangeProperty<V>(initialValue)
    }
}

class ObservableValue<T: Any?>(initialValue: T) : Observable() {
    var value: T by notifyOnChange<T>(initialValue)
}

fun <T : IObservable> T.addObserver(fireImmediately: Boolean = false, callback: (T) -> Unit): RemovableObserver<T> {
    val observer = RemovableObserver(this, callback)
    addObserver(observer)
    if (fireImmediately) callback(this)
    return observer
}

class RemovableObserver<T : IObservable>(
    private val observable: T,
    private val callback: (T) -> Unit
) : Observer {
    override fun notifyChanged() = callback.invoke(observable)
    fun remove() = observable.removeObserver(this)
}

class NotifyOnChangeProperty<V>(initialValue: V) : ReadWriteProperty<Observable, V> {
    private var value = initialValue

    override fun getValue(thisRef: Observable, property: KProperty<*>): V = value

    override fun setValue(thisRef: Observable, property: KProperty<*>, value: V) {
        if (this.value != value) {
            this.value = value
            thisRef.notifyChanged()
        }
    }
}
package baaahs.ui

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public interface IObservable {
    public fun addObserver(observer: Observer): Observer
    public fun removeObserver(observer: Observer)
}

public open class Observable : IObservable {
    private val observers = mutableListOf<Observer>()

    protected fun anyObservers(): Boolean = observers.isNotEmpty()

    override fun addObserver(observer: Observer): Observer {
        observers.add(observer)
        return observer
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    public fun notifyChanged() {
        observers.forEach { it.notifyChanged() }
    }

    protected fun <V> notifyOnChange(initialValue: V): ReadWriteProperty<Observable, V> {
        return NotifyOnChangeProperty<V>(initialValue)
    }
}

public class ObservableValue<T: Any?>(initialValue: T) : Observable() {
    public var value: T by notifyOnChange<T>(initialValue)
}

public fun <T : IObservable> T.addObserver(fireImmediately: Boolean = false, callback: (T) -> Unit): RemovableObserver<T> {
    val observer = RemovableObserver(this, callback)
    addObserver(observer)
    if (fireImmediately) callback(this)
    return observer
}

public class RemovableObserver<T : IObservable>(
    private val observable: T,
    private val callback: (T) -> Unit
) : Observer {
    override fun notifyChanged(): Unit = callback.invoke(observable)
    public fun remove(): Unit = observable.removeObserver(this)
}

public class NotifyOnChangeProperty<V>(initialValue: V) : ReadWriteProperty<Observable, V> {
    private var value = initialValue

    override fun getValue(thisRef: Observable, property: KProperty<*>): V = value

    override fun setValue(thisRef: Observable, property: KProperty<*>, value: V) {
        if (this.value != value) {
            this.value = value
            thisRef.notifyChanged()
        }
    }
}
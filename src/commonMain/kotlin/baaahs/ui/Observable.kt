package baaahs.ui

interface IObservable {
    fun addObserver(observer: Observer): Observer
    fun removeObserver(observer: Observer)
}

open class Observable : IObservable {
    private val observers = mutableListOf<Observer>()

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

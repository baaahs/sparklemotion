package baaahs.ui

open class Observable {
    private val observers = mutableListOf<Observer>()

    fun addObserver(observer: Observer): Observer {
        observers.add(observer)
        return observer
    }

    fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    fun notifyChanged() {
        observers.forEach { it.notifyChanged() }
    }
}

fun <T : Observable> T.addObserver(callback: (T) -> Unit): RemovableObserver<T> {
    val observer = RemovableObserver(this, callback)
    addObserver(observer)
    return observer
}

class RemovableObserver<T : Observable>(
    val observable: T,
    private val callback: (T) -> Unit
) : Observer {
    override fun notifyChanged() = callback.invoke(observable)
    fun remove() = observable.removeObserver(this)
}

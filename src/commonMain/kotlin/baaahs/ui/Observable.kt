package baaahs.ui

open class Observable {
    private val observers = mutableListOf<Observer>()

    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    fun notifyChanged() {
        observers.forEach { it.notifyChanged() }
    }
}

package baaahs.ui

open class Facade {
    private val observers = mutableListOf<Observer>()

    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    fun notify() {
        observers.forEach { it.notifyChanged() }
    }

    interface Observer {
        fun notifyChanged()
    }
}

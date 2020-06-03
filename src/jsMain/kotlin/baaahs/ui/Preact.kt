package baaahs.ui

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Preact {
    var firstTime = false
    var dataIndex = 0
    var sideEffectIndex = 0
    val state = react.useState {
        firstTime = true
        Context()
    }
    val counter = react.useState { 0 }
    var internalCounter = 0

    fun <T> state(valueInitializer: () -> T): Data<T> {
        @Suppress("UNREACHABLE_CODE")
        return if (firstTime) {
            val data = Data(valueInitializer()) {
                counter.second(internalCounter++)
            }
            state.first.data.add(data)
            return data
        } else {
            @Suppress("UNCHECKED_CAST")
            return state.first.data[dataIndex++] as Data<T>
        }
    }

    fun sideEffect(name: String, vararg watch: Any?, callback: () -> Unit) {
        if (firstTime) {
            val sideEffect = SideEffect(watch)
            state.first.sideEffects.add(sideEffect)
            callback()
        } else {
            val sideEffect = state.first.sideEffects[sideEffectIndex++]
            if (watch.zip(sideEffect.lastWatchValues).all { (a, b) ->
                    if (a is String && b is String) {
                        a == b
                    } else if (a is Number && b is Number) {
                        a == b
                    } else {
                        a === b
                    }
            }) {
                println("Not running side effect $name " +
                        "(${watch.truncateStrings(12)} == ${sideEffect.lastWatchValues.truncateStrings(12)}")
            } else {
                println("Running side effect $name " +
                        "(${watch.truncateStrings(12)} != ${sideEffect.lastWatchValues.truncateStrings(12)}")
                sideEffect.lastWatchValues = watch
                callback()
            }
        }
    }

    class Context {
        val data: MutableList<Data<*>> = mutableListOf()
        val sideEffects: MutableList<SideEffect> = mutableListOf()
    }

    class Data<T>(initialValue: T, private val onChange: () -> Unit): ReadWriteProperty<Any?, T> {
        var value: T = initialValue

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            println("${property.name} := ${value?.toString()?.truncate(12)}")
            this.value = value
            onChange()
        }
    }

    class SideEffect(
        var lastWatchValues: Array<out Any?>
    )
}


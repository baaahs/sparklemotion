package baaahs.ui

import baaahs.Logger
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Preact {
    private var firstTime = false
    private var dataIndex = 0
    private var sideEffectIndex = 0
    private val state = react.useState {
        firstTime = true
        Context()
    }
    private val counter = react.useState { 0 }
    private var internalCounter = 0

    init {
        react.useEffectWithCleanup(emptyList()) {
            return@useEffectWithCleanup {
                state.first.sideEffects.forEach { it.runCleanups() }
            }
        }
    }

    fun <T> state(valueInitializer: () -> T): ReadWriteProperty<Any?, T> {
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

    fun sideEffect(name: String, vararg watch: Any?, callback: SideEffect.() -> Unit) {
        return if (firstTime) {
            val sideEffect = SideEffect(watch)
            state.first.sideEffects.add(sideEffect)
            sideEffect.callback()
        } else {
            val sideEffect = state.first.sideEffects[sideEffectIndex++]
            sideEffect.firstTime = false
            if (watch.zip(sideEffect.lastWatchValues).all { (a, b) ->
                    if (a is String && b is String) {
                        a == b
                    } else if (a is Number && b is Number) {
                        a == b
                    } else {
                        a === b
                    }
            }) {
                logger.debug {
                    "Not running side effect $name " +
                            "(${watch.truncateStrings(12)} == ${sideEffect.lastWatchValues.truncateStrings(12)}"
                }
            } else {
                logger.debug {
                    "Running side effect $name " +
                            "(${watch.truncateStrings(12)} != ${sideEffect.lastWatchValues.truncateStrings(12)}"
                }
                sideEffect.runCleanups()
                sideEffect.lastWatchValues = watch
                sideEffect.callback()
            }
        }
    }

    private class Context {
        val data: MutableList<Data<*>> = mutableListOf()
        val sideEffects: MutableList<SideEffect> = mutableListOf()
    }

    private class Data<T>(initialValue: T, private val onChange: () -> Unit): ReadWriteProperty<Any?, T> {
        var value: T = initialValue

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            logger.debug { "${property.name} := ${value?.toString()?.truncate(12)}" }
            this.value = value
            onChange()
        }
    }

    class SideEffect(
        internal var lastWatchValues: Array<out Any?>
    ) {
        internal var firstTime: Boolean = true
        internal var cleanups: MutableList<() -> Unit>? = null

        fun withCleanup(cleanup: () -> Unit) {
            if (firstTime) {
                if (cleanups == null) cleanups = mutableListOf()
                cleanups!!.add(cleanup)
            }
        }

        internal fun runCleanups() {
            cleanups?.forEach { it.invoke() }
            cleanups?.clear()
            firstTime = true // re-collect cleanups
        }
    }

    companion object {
        private val logger = Logger("Preact")
    }
}


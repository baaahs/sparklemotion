package baaahs.util

import baaahs.internalTimerClock
import kotlin.math.roundToInt
import kotlin.properties.ReadOnlyProperty

open class Stats {
    private val statistics = mutableMapOf<String, Statistic>()

    protected fun statistic(): ReadOnlyProperty<Stats, Statistic> {
        return ReadOnlyProperty { thisRef, property ->
            thisRef.statistics.getOrPut(property.name) { Statistic(property.name) }
        }
    }

    fun summarize(): String = statistics.values.joinToString("\n") { it.summarize() }

    class Statistic(val name: String) {
        var calls = 0
        var elapsedTime = Interval(0)

        fun <T> time(block: () -> T): T {
            val startTime = internalTimerClock.now()
            return try {
                block.invoke()
            } finally {
                calls++
                elapsedTime += internalTimerClock.now() - startTime
            }
        }

        fun summarize(): String {
            val avgTimeMs = elapsedTime / calls * 1000
            return "$name: $calls calls, avg ${avgTimeMs.roundToInt()}ms, total ${(elapsedTime * 1000).roundToInt()}ms"
        }
    }
}
package baaahs.util

import baaahs.internalTimerClock
import kotlin.math.roundToInt
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class Stats {
    private val statistics = mutableMapOf<String, Statistic>()

    protected val statistic = PropertyDelegateProvider { thisRef: Stats, property ->
        val statistic = thisRef.statistics.getOrPut(property.name) { Statistic(property.name) }
        ReadOnlyProperty<Stats, Statistic> { _, _ -> statistic }
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

        suspend fun <T> stime(block: suspend () -> T): T {
            val startTime = internalTimerClock.now()
            return try {
                block.invoke()
            } finally {
                calls++
                elapsedTime += internalTimerClock.now() - startTime
            }
        }

        fun summarize(): String {
            val avgTimeMs = if (calls > 0) {
                (elapsedTime / calls * 1000).roundToInt()
            } else "—"
            return "$name: $calls calls, avg ${avgTimeMs}ms, total ${(elapsedTime * 1000).roundToInt()}ms"
        }
    }
}
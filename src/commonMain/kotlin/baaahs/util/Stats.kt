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

    val all = statistics.values

    fun summarize(): String = all.joinToString("\n") { it.summarize() }

    class Statistic(val name: String) {
        var calls = 0
        var elapsedTime = Interval(0)
        val elapsedTimeMs get() = (elapsedTime * 1000).roundToInt()
        val averageTime get() = if (calls > 0) elapsedTime / calls else null
        val averageTimeMs get() = averageTime?.times(1000)?.roundToInt()

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
            return "$name: $calls calls, avg ${averageTimeMs ?: "-"}ms, total ${elapsedTimeMs}ms"
        }
    }
}
package baaahs.util

import baaahs.internalTimerClock
import kotlin.math.roundToInt
import kotlin.math.roundToLong

typealias Time = Double
fun Interval(n: Number): Time = n.toDouble()

fun Time.elapsedMs() = ((internalTimerClock.now() - this) * 10000).roundToInt() / 10.0

interface Clock {
    fun now(): Time
}

fun Time.asMillis(): Long = (this * 1000).roundToLong()
fun Float.asMillis(): Int = (this * 1000).roundToInt()
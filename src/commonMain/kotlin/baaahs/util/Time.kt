package baaahs.util

import baaahs.internalTimerClock
import kotlin.math.roundToInt
import kotlin.math.roundToLong

typealias Time = Double
fun Interval(n: Number): Time = n.toDouble()

fun Time.elapsedMs() = ((internalTimerClock.now() - this) * 10000).roundToInt() / 10.0

/** Truncate time to 4 digits of seconds so we don't overflow a GLSL float. */
fun Time.makeSafeForGlsl() = (this % 10000.0).toFloat()

fun Time.isBefore(otherTime: Time) = this < otherTime

interface Clock {
    fun now(): Time
}

fun Time.asMillis(): Long = (this * 1000).roundToLong()
fun Float.asMillis(): Int = (this * 1000).roundToInt()

fun Time.toHHMMSS(): String {
    val seconds = this.toInt()
    val hours = seconds / 3600
    val minutes = seconds / 60 % 60
    val secs = seconds % 60
    return buildString {
        if (hours > 0) append(hours).append(":")
        if (isEmpty()) {
            append(minutes.toString()).append(":")
        } else {
            append("0${minutes}".takeLast(2)).append(":")
        }
        append("0${secs}".takeLast(2))

    }
}
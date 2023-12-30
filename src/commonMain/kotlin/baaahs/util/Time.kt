package baaahs.util

import baaahs.internalTimerClock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.math.roundToInt
import kotlin.math.roundToLong

typealias Time = Double

/** Formats elapsed time as in 123.4ms. */
fun Instant.elapsedMs() = ((internalTimerClock.now() - this).inWholeMicroseconds / 100)
        .toDouble() / 10

/** Truncate time to 4 digits of seconds so we don't overflow a GLSL float. */
fun Time.makeSafeForGlsl() = (this % 10000.0).toFloat()

fun Instant.isBefore(otherTime: Instant) = this < otherTime

interface Clock {
    fun now(): Instant
    fun tz(): TimeZone = TimeZone.currentSystemDefault()
}

fun Time.asInstant(): Instant =
    Instant.fromEpochSeconds(
        this.toLong(),
        (this % 1.0 * 1_000_000_000).toInt()
    )

fun Time.asMillis(): Long = (this * 1000).roundToLong()
fun Float.asMillis(): Int = (this * 1000).roundToInt()

val Instant.unixMillis: Long get() = toEpochMilliseconds()
val Instant.asDoubleSeconds: Time get() = epochSeconds + (nanosecondsOfSecond / 1_000_000_000.0)
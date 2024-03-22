package baaahs.util

import baaahs.internalTimerClock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

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

fun Duration.toHHMMSS(): String {
    val seconds = this.toInt(DurationUnit.SECONDS)
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

val Instant.unixMillis: Long get() = toEpochMilliseconds()
val Instant.asDoubleSeconds: Time get() = epochSeconds + (nanosecondsOfSecond / 1_000_000_000.0)

val Float.seconds: Duration get() = this.toDouble().seconds
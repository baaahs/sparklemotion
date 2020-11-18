package baaahs.util

import kotlin.math.roundToLong

typealias Time = Double

interface Clock {
    fun now(): Time
}

fun Time.asMillis(): Long = (this * 1000).roundToLong()
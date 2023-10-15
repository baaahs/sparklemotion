package baaahs.util

import kotlin.js.Date
import kotlin.math.roundToLong

object JsClock : Clock {
    override fun now(): Time =
        Date.now() / 1000.0

    override fun nowMillis(): Long =
        Date.now().roundToLong()
}
package baaahs.util

import kotlin.js.Date

object JsClock : Clock {
    override fun now(): Time = Date.now() / 1000.0
}
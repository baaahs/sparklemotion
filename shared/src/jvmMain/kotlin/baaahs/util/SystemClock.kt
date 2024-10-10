package baaahs.util

import kotlinx.datetime.Instant

object SystemClock : Clock {
    override fun now(): Instant {
        return kotlinx.datetime.Clock.System.now()
    }
}
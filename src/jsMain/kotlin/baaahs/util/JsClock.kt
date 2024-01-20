package baaahs.util

import kotlinx.datetime.Instant

object JsClock : Clock {
    override fun now(): Instant = kotlinx.datetime.Clock.System.now()
}
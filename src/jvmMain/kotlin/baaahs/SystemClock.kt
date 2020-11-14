package baaahs

import baaahs.util.Clock
import baaahs.util.Time

class SystemClock : Clock {
    override fun now(): Time {
        return System.currentTimeMillis().toDouble() / 1000.0
    }
}
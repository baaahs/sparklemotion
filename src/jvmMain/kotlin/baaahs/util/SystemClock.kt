package baaahs.util

object SystemClock : Clock {
    override fun now(): Time {
        return System.currentTimeMillis().toDouble() / 1000.0
    }
}
package baaahs.util

object SystemClock : Clock {
    override fun now(): Time =
        System.currentTimeMillis().toDouble() / 1000.0

    override fun nowMillis(): Long =
        System.currentTimeMillis()
}
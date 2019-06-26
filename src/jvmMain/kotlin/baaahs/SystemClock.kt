package baaahs

class SystemClock : Clock {
    override fun now(): Time {
        return System.currentTimeMillis().toDouble()
    }
}
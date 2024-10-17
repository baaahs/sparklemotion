package baaahs.imaging

import baaahs.util.Clock

class Animator(
    private val durationsMs: List<Int>,
    private val clock: Clock
) {
    val startedAt = clock.now()
    val durationMs: Int = durationsMs.sum()

    fun getCurrentFrame(): Int =
        getFrameAt((clock.now() - startedAt).inWholeMilliseconds.toInt())

    fun getFrameAt(offsetTimeMs: Int): Int {
        val offsetTimeModStartMs = offsetTimeMs % durationMs
        var elapsedMs = 0
        return durationsMs.indexOfFirst { durationMs ->
            (offsetTimeModStartMs >= elapsedMs && offsetTimeModStartMs < elapsedMs + durationMs)
                .also { elapsedMs += durationMs }
        }
    }
}
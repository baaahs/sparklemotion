package baaahs

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class BeatData(
    /** Some moment in history when we saw a beat 1. */
    val measureStartTimeMs: Time,

    val beatIntervalMs: Int,

    val beatsPerMeasure: Int = 4,

    val confidence: Float = 1f
) {
    @Transient val bpm: Float
        get() = (60_000 / beatIntervalMs).toFloat()

    fun beatWithinMeasure(clock: Clock): Float {
        val elapsedSinceStartOfMeasure = clock.now() - measureStartTimeMs
        return ((elapsedSinceStartOfMeasure / beatIntervalMs).toFloat()) % beatsPerMeasure + 1
    }
}

interface BeatSource {
    fun getBeatData(): BeatData
}

typealias Time = Double

interface Clock {
    fun now(): Time
}

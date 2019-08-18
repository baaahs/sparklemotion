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
        get() {
            if (beatIntervalMs == 0) return 0.0.toFloat()
            return (60_000 / beatIntervalMs).toFloat()
        }

    fun beatWithinMeasure(clock: Clock): Float {
        val elapsedSinceStartOfMeasure = clock.now() - measureStartTimeMs
        return ((elapsedSinceStartOfMeasure / beatIntervalMs).toFloat()) % beatsPerMeasure
    }

    fun timeSinceMeasure(clock: Clock): Float {
        val elapsedSinceStartOfMeasure = clock.now() - measureStartTimeMs
        return (elapsedSinceStartOfMeasure / beatIntervalMs).toFloat()
    }

    /** Returns 1.0 if we're on a beat, 0.0 when we're furthest from the last beat,
     * and anywhere in between otherwise. */
    fun fractionTilNextBeat(clock: Clock): Float =
        1 - beatWithinMeasure(clock) % 1.0f

    /** Returns 1.0 if we're on the start of the measure, 0.0 when we're furthest from the start of the measure,
     * and anywhere in between otherwise. */
    fun fractionTilNextMeasure(clock: Clock): Float =
        1 - timeSinceMeasure(clock)
}


interface BeatSource {
    fun getBeatData(): BeatData
}

typealias Time = Double

interface Clock {
    fun now(): Time
}

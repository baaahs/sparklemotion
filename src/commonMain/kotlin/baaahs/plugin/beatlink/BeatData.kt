package baaahs.plugin.beatlink

import baaahs.ui.IObservable
import baaahs.ui.Observable
import baaahs.util.Clock
import baaahs.util.Time
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Serializable
data class BeatData(
    /** Some moment in history when we saw a beat 1. */
    val measureStartTime: Time,

    val beatIntervalMs: Int,

    val beatsPerMeasure: Int = 4,

    val confidence: Float = 1f
) {
    private val beatIntervalSec: Double get() = beatIntervalMs / 1000.0

    val bpm: Float
        get() {
            if (beatIntervalMs == 0) return 0.0.toFloat()
            return (60_000 / beatIntervalMs).toFloat()
        }

    fun beatWithinMeasure(clock: Clock): Float {
        if (beatIntervalMs == 0) return -1f
        val elapsedSinceStartOfMeasure = clock.now() - measureStartTime
        return ((elapsedSinceStartOfMeasure / beatIntervalSec).toFloat()) % beatsPerMeasure
    }

    fun timeSinceMeasure(clock: Clock): Float {
        if (beatIntervalMs == 0) return -1f
        val elapsedSinceStartOfMeasure = clock.now() - measureStartTime
        return (elapsedSinceStartOfMeasure / beatIntervalSec).toFloat()
    }

    /**
     * Returns 1.0 if we're on a beat, 0.0 when we're furthest from the last beat,
     * and anywhere in between otherwise.
     */
    fun fractionTillNextBeat(clock: Clock): Float {
        return if (beatIntervalMs == 0) -1f else return clamp(sineWithEarlyAttack(clock)) * confidence
    }

    fun millisTillNextBeat(clock: Clock): Int {
        val elapsedSinceStartOfMeasure = (clock.now() - measureStartTime)
        return (beatIntervalSec - elapsedSinceStartOfMeasure % beatIntervalSec).toInt()
    }

    // TODO: make these into pluggable strategies that can be selected by shows.
    private fun sineWithEarlyAttack(clock: Clock): Float {
        return (((sin(beatWithinMeasure(clock) % 1f - .87) * 2 * PI) * 1.25 + 1) / 2.0).toFloat()
    }

    private fun sawtooth(clock: Clock): Float {
        return 1 - beatWithinMeasure(clock) % 1.0f
    }

    /** Returns 1.0 if we're on the start of the measure, 0.0 when we're furthest from the start of the measure,
     * and anywhere in between otherwise. */
    fun fractionTillNextMeasure(clock: Clock): Float =
        if (beatIntervalMs == 0) -1f else 1 - timeSinceMeasure(clock)

    private fun clamp(f: Float): Float = min(1f, max(f, 0f))
}


interface BeatSource : IObservable {
    fun getBeatData(): BeatData

    object None : Observable(), BeatSource {
        val none = BeatData(0.0, 0, 4, 0f)

        override fun getBeatData(): BeatData = none
    }
}


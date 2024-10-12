package baaahs.plugin.beatlink

import baaahs.FakeClock
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test
import kotlin.test.assertNotEquals

class BeatDataTest {
    val realisticTime = 1605473794.0

    @Test
    fun calculateBpm() {
        expect(BeatData(0.0, 500).bpm).toBe(120f)
    }

    @Test
    fun calculateBeatWithinMeasure() {
        expect(BeatData(0.0, 500).beatWithinMeasure(FakeClock(0.0))).toBe(0f)
        expect(BeatData(0.0, 500).beatWithinMeasure(FakeClock(2.0))).toBe(0f)

        expect(BeatData(0.0, 500).beatWithinMeasure(FakeClock(0.5))).toBe(1f)
        expect(BeatData(0.0, 500).beatWithinMeasure(FakeClock(2.5))).toBe(1f)

        expect(BeatData(0.0, 500).beatWithinMeasure(FakeClock(1.0))).toBe(2f)
        expect(BeatData(0.0, 500).beatWithinMeasure(FakeClock(3.0))).toBe(2f)

        expect(BeatData(0.0, 500).beatWithinMeasure(FakeClock(1.5))).toBe(3f)
        expect(BeatData(0.0, 500).beatWithinMeasure(FakeClock(3.5))).toBe(3f)
    }

    @Test
    fun whenConfidenceIsBelowOne_fractionTillNextBeatDecays() {
        val beatData = BeatData(0.0, 500)
        val clock = FakeClock(0.49)

        assertNotEquals(0f, beatData.fractionTillNextBeat(clock))
        expect(beatData.copy(confidence = .5f).fractionTillNextBeat(clock)).toBe(beatData.fractionTillNextBeat(clock) / 2)
    }

    @Test
    fun whenNoBeatDataPresent_beatAndTimeAreNegativeOne() {
        expect(BeatData(0.0, 0).beatWithinMeasure(FakeClock(0.0))).toBe(-1f)
        expect(BeatData(0.0, 0).timeSinceMeasure(FakeClock(0.0))).toBe(-1f)
        expect(BeatData(0.0, 0).fractionTillNextBeat(FakeClock(0.0))).toBe(-1f)
        expect(BeatData(0.0, 0).fractionTillNextBeat(FakeClock(0.0))).toBe(-1f)
    }

    @Test
    fun testMillisTillNextBeat() {
        expect(BeatData(0.0, 100).millisTillNextBeat(FakeClock(0.025))).toBe(75)
        expect(BeatData(0.0, 100).millisTillNextBeat(FakeClock(0.325))).toBe(75)

        expect(BeatData(0.0, 100).millisTillNextBeat(FakeClock(realisticTime + 0.025))).toBe(75)
        expect(BeatData(0.0, 100).millisTillNextBeat(FakeClock(realisticTime + 0.325))).toBe(75)
    }
}
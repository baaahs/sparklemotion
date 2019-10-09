package baaahs

import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.expect

class BeatDataTest {
    @Test
    fun calculateBpm() {
        expect(120f) { BeatData(0.0, 500).bpm }
    }

    @Test
    fun calculateBeatWithinMeasure() {
        expect(0f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(0.0)) }
        expect(0f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(2.0)) }

        expect(1f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(0.5)) }
        expect(1f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(2.5)) }

        expect(2f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(1.0)) }
        expect(2f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(3.0)) }

        expect(3f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(1.5)) }
        expect(3f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(3.5)) }
    }

    @Test
    fun whenConfidenceIsBelowOne_fractionTillNextBeatDecays() {
        val beatData = BeatData(0.0, 500)
        val clock = FakeClock(0.49)

        assertNotEquals(0f, beatData.fractionTillNextBeat(clock))
        expect(beatData.fractionTillNextBeat(clock) / 2) {
            beatData.copy(confidence = .5f).fractionTillNextBeat(clock)
        }
    }

    @Test
    fun whenNoBeatDataPresent_beatAndTimeAreNegativeOne() {
        expect(-1f) { BeatData(0.0, 0).beatWithinMeasure(FakeClock(0.0)) }
        expect(-1f) { BeatData(0.0, 0).timeSinceMeasure(FakeClock(0.0)) }
        expect(-1f) { BeatData(0.0, 0).fractionTillNextBeat(FakeClock(0.0)) }
        expect(-1f) { BeatData(0.0, 0).fractionTillNextBeat(FakeClock(0.0)) }
    }
}
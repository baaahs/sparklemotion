package baaahs

import kotlin.test.Test
import kotlin.test.expect

class BeatDataTest {
    @Test
    fun calculateBpm() {
        expect(120f) { BeatData(0.0, 500).bpm }
    }

    @Test
    fun calculateBeatWithinMeasure() {
        expect(0f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(0.0)) }
        expect(0f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(2000.0)) }

        expect(1f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(500.0)) }
        expect(1f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(2500.0)) }

        expect(2f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(1000.0)) }
        expect(2f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(3000.0)) }

        expect(3f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(1500.0)) }
        expect(3f) { BeatData(0.0, 500).beatWithinMeasure(FakeClock(3500.0)) }
    }
}
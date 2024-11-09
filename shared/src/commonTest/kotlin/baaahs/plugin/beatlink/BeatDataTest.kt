package baaahs.plugin.beatlink

import baaahs.FakeClock
import io.kotest.matchers.*
import kotlin.test.Test
import kotlin.test.assertNotEquals

class BeatDataTest {
    val realisticTime = 1605473794.0

    @Test
    fun calculateBpm() {
        BeatData(0.0, 500).bpm.shouldBe(120f)
    }

    @Test
    fun calculateBeatWithinMeasure() {
        BeatData(0.0, 500).beatWithinMeasure(FakeClock(0.0)).shouldBe(0f)
        BeatData(0.0, 500).beatWithinMeasure(FakeClock(2.0)).shouldBe(0f)

        BeatData(0.0, 500).beatWithinMeasure(FakeClock(0.5)).shouldBe(1f)
        BeatData(0.0, 500).beatWithinMeasure(FakeClock(2.5)).shouldBe(1f)

        BeatData(0.0, 500).beatWithinMeasure(FakeClock(1.0)).shouldBe(2f)
        BeatData(0.0, 500).beatWithinMeasure(FakeClock(3.0)).shouldBe(2f)

        BeatData(0.0, 500).beatWithinMeasure(FakeClock(1.5)).shouldBe(3f)
        BeatData(0.0, 500).beatWithinMeasure(FakeClock(3.5)).shouldBe(3f)
    }

    @Test
    fun whenConfidenceIsBelowOne_fractionTillNextBeatDecays() {
        val beatData = BeatData(0.0, 500)
        val clock = FakeClock(0.49)

        assertNotEquals(0f, beatData.fractionTillNextBeat(clock))
        beatData.copy(confidence = .5f).fractionTillNextBeat(clock).shouldBe(beatData.fractionTillNextBeat(clock) / 2)
    }

    @Test
    fun whenNoBeatDataPresent_beatAndTimeAreNegativeOne() {
        BeatData(0.0, 0).beatWithinMeasure(FakeClock(0.0)).shouldBe(-1f)
        BeatData(0.0, 0).timeSinceMeasure(FakeClock(0.0)).shouldBe(-1f)
        BeatData(0.0, 0).fractionTillNextBeat(FakeClock(0.0)).shouldBe(-1f)
        BeatData(0.0, 0).fractionTillNextBeat(FakeClock(0.0)).shouldBe(-1f)
    }

    @Test
    fun testMillisTillNextBeat() {
        BeatData(0.0, 100).millisTillNextBeat(FakeClock(0.025)).shouldBe(75)
        BeatData(0.0, 100).millisTillNextBeat(FakeClock(0.325)).shouldBe(75)

        BeatData(0.0, 100).millisTillNextBeat(FakeClock(realisticTime + 0.025)).shouldBe(75)
        BeatData(0.0, 100).millisTillNextBeat(FakeClock(realisticTime + 0.325)).shouldBe(75)
    }
}
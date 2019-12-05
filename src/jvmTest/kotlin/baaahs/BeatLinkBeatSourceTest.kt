package baaahs

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.deepsymmetry.beatlink.Beat
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotEquals
import kotlin.test.expect

class BeatLinkBeatSourceTest {
    private lateinit var fakeClock: FakeClock
    private lateinit var beatSource: BeatLinkBeatSource

    @Before
    fun setUp() {
        fakeClock = FakeClock(10.0)
        beatSource = BeatLinkBeatSource(fakeClock)
    }

    @Test
    fun beforeAnyBeatsReceived_currentBeatIsZero() {
        expect(BeatData(0.0, 0, confidence = 0f)) { beatSource.currentBeat }
    }

    @Test
    fun whenNewBeatIsReceived_beatDataIsUpdated() {
        beatSource.channelsOnAir(hashSetOf(1))
        beatSource.newBeat(mockBeat(123.4, 1))
        expect(BeatData(10.0, 486, confidence = 1f)) { beatSource.currentBeat }
    }

    @Test
    fun whenMidBarNewBeatIsReceived_beatDataIsUpdated() {
        beatSource.channelsOnAir(hashSetOf(1))
        beatSource.newBeat(mockBeat(123.4, 2))
        expect(BeatData(10.0 - 60 / 123.4, 486, confidence = 1f)) { beatSource.currentBeat }
    }

    @Test
    fun whenMidBarNewBeatIsReceivedWithSmallMeasureStartVariance_beatDataIsNotUpdated() {
        beatSource.channelsOnAir(hashSetOf(1))
        beatSource.newBeat(mockBeat(123.4, 1))

        fakeClock.now += 0.486
        beatSource.newBeat(mockBeat(123.4, 2))
        expect(BeatData(10.0, 486, confidence = 1f)) { beatSource.currentBeat }

        fakeClock.now += 0.490
        beatSource.newBeat(mockBeat(123.4, 3))
        assertEquals(10.003, beatSource.currentBeat.measureStartTime, 0.0009)
        assertEquals(486, beatSource.currentBeat.beatIntervalMs)
    }

    @Test
    fun whenMidBarNewBeatIsReceivedWithSmallMeasureStartVarianceAndDifferentTempo_beatDataIsUpdated() {
        beatSource.channelsOnAir(hashSetOf(1))
        beatSource.newBeat(mockBeat(123.4, 1))

        fakeClock.now += 0.486
        beatSource.newBeat(mockBeat(123.5, 2))
        assertNotEquals(10.0, beatSource.currentBeat.measureStartTime)
        assertEquals(10.0, beatSource.currentBeat.measureStartTime, 0.0009)
        assertEquals(485, beatSource.currentBeat.beatIntervalMs)
    }

    private fun mockBeat(_effectiveTempo: Double, _beatWithinBar: Int, _deviceNumber: Int = 1): Beat {
        return mockk {
            every { effectiveTempo } returns _effectiveTempo
            every { beatWithinBar } returns _beatWithinBar
            every { deviceNumber } returns _deviceNumber
            every { deviceName } returns "Fake CDJ"
        }
    }
}

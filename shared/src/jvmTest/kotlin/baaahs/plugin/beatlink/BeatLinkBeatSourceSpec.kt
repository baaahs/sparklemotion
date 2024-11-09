package baaahs.plugin.beatlink

import baaahs.Color
import baaahs.FakeClock
import baaahs.describe
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.doubles.shouldBeWithinPercentageOf
import io.mockk.every
import io.mockk.mockk
import org.deepsymmetry.beatlink.Beat
import kotlin.math.roundToInt
import kotlin.test.assertNotEquals
import kotlin.time.Duration.Companion.seconds

object BeatLinkBeatSourceSpec : DescribeSpec({
    describe<BeatLinkBeatSource> {
        val fakeClock by value { FakeClock(10.0) }
        val beatSource by value { BeatLinkBeatSource(fakeClock) }

        context("beforeAnyBeatsReceived") {
            it("currentBeatIsZero") {
                beatSource.currentBeat.shouldBe(BeatData(0.0, 0, confidence = 0f))
            }
        }

        context("whenNewBeatIsReceived") {
            it("beatDataIsUpdated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 1))
                beatSource.currentBeat.shouldBe(BeatData(10.0, 486, confidence = 1f))
            }
        }

        context("whenMidBarNewBeatIsReceived") {
            it("beatDataIsUpdated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 2))
                beatSource.currentBeat.shouldBe(BeatData(10.0 - 60 / 123.4, 486, confidence = 1f))
            }
        }

        context("whenMidBarNewBeatIsReceivedWithSmallMeasureStartVariance") {
            it("beatDataIsNotUpdated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 1))

                fakeClock.time += 0.486.seconds
                beatSource.newBeat(mockBeat(123.4, 2))
                beatSource.currentBeat.shouldBe(BeatData(10.0, 486, confidence = 1f))

                fakeClock.time += 0.490.seconds
                beatSource.newBeat(mockBeat(123.4, 3))
                beatSource.currentBeat.measureStartTime.shouldBeWithinPercentageOf(10.003, 0.009)
                beatSource.currentBeat.beatIntervalMs.shouldBe(486)
            }
        }

        context("whenMidBarNewBeatIsReceivedWithSmallMeasureStartVarianceAndDifferentTempo") {
            it("beatDataIsUpdated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 1))

                fakeClock.time += 0.486.seconds
                beatSource.newBeat(mockBeat(123.5, 2))
                assertNotEquals(10.0, beatSource.currentBeat.measureStartTime)
                beatSource.currentBeat.measureStartTime.shouldBeWithinPercentageOf(10.0, 0.009)
                beatSource.currentBeat.beatIntervalMs.shouldBe(485)
            }
        }

        describe("confidence adjustment") {
            beforeEach {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(120.0, 1))
            }

            context("when we hear beat updates at appropriate intervals") {
                beforeEach {
                    fakeClock.time += 2.0.seconds
                    beatSource.adjustConfidence()
                }

                it("retains high confidence") {
                    beatSource.currentBeat.confidence.shouldBe(1f)
                }
            }

            context("when we haven't heard a beat update for more than a measure") {
                beforeEach {
                    fakeClock.time += 2.1.seconds
                    beatSource.adjustConfidence()
                }

                it("reduces the confidence") {
                    beatSource.currentBeat.confidence.shouldBe(.99f)
                }
            }
        }

        context("waveforms") {
            val playerState by value { PlayerState(encodedWaveform = "074080bf1a111111", waveformScale = 1) }

            it("#sampleCount") {
                playerState.waveform?.sampleCount.shouldBe(2)
            }

            it("#totalTimeMs") {
                playerState.waveform?.totalTimeMs?.roundToInt().shouldBe(13)
            }

            it("encodes compactly") {
                PlayerState().withWaveform(1) {
                    add(7, Color(0x40, 0x80, 0xbf))
                    add(26, Color(0x11, 0x11, 0x11))
                }.shouldBe(playerState)
            }

            it("decodes correctly") {
                playerState.waveform?.heightAt(0)
                    .shouldBe(7)
                playerState.waveform?.colorAt(0)
                    .shouldBe(baaahs.Color(0x40, 0x80, 0xbf))

                playerState.waveform?.heightAt(1)
                    .shouldBe(26)
                playerState.waveform?.colorAt(1)
                    .shouldBe(baaahs.Color(0x11, 0x11, 0x11))
            }
        }
    }
})

fun mockBeat(_effectiveTempo: Double, _beatWithinBar: Int, _deviceNumber: Int = 1): Beat {
    return mockk {
        every { effectiveTempo } returns _effectiveTempo
        every { beatWithinBar } returns _beatWithinBar
        every { deviceNumber } returns _deviceNumber
        every { deviceName } returns "Fake CDJ"
    }
}

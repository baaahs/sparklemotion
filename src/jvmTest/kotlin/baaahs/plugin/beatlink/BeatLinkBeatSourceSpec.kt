package baaahs.plugin.beatlink

import baaahs.Color
import baaahs.FakeClock
import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.every
import io.mockk.mockk
import org.deepsymmetry.beatlink.Beat
import org.spekframework.spek2.Spek
import kotlin.test.assertNotEquals

object BeatLinkBeatSourceSpec : Spek({
    describe<BeatLinkBeatSource> {
        val fakeClock by value { FakeClock(10.0) }
        val beatSource by value { BeatLinkBeatSource(fakeClock) }

        context("beforeAnyBeatsReceived") {
            it("currentBeatIsZero") {
                expect(beatSource.currentBeat).toBe(BeatData(0.0, 0, confidence = 0f))
            }
        }

        context("whenNewBeatIsReceived") {
            it("beatDataIsUpdated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 1))
                expect(beatSource.currentBeat).toBe(BeatData(10.0, 486, confidence = 1f))
            }
        }

        context("whenMidBarNewBeatIsReceived") {
            it("beatDataIsUpdated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 2))
                expect(beatSource.currentBeat).toBe(BeatData(10.0 - 60 / 123.4, 486, confidence = 1f))
            }
        }

        context("whenMidBarNewBeatIsReceivedWithSmallMeasureStartVariance") {
            it("beatDataIsNotUpdated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 1))

                fakeClock.time += 0.486
                beatSource.newBeat(mockBeat(123.4, 2))
                expect(beatSource.currentBeat).toBe(BeatData(10.0, 486, confidence = 1f))

                fakeClock.time += 0.490
                beatSource.newBeat(mockBeat(123.4, 3))
                expect(beatSource.currentBeat.measureStartTime).toBeWithErrorTolerance(10.003, 0.0009)
                expect(beatSource.currentBeat.beatIntervalMs).toBe(486)
            }
        }

        context("whenMidBarNewBeatIsReceivedWithSmallMeasureStartVarianceAndDifferentTempo") {
            it("beatDataIsUpdated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 1))

                fakeClock.time += 0.486
                beatSource.newBeat(mockBeat(123.5, 2))
                assertNotEquals(10.0, beatSource.currentBeat.measureStartTime)
                expect(beatSource.currentBeat.measureStartTime).toBeWithErrorTolerance(10.0, 0.0009)
                expect(beatSource.currentBeat.beatIntervalMs).toBe(485)
            }
        }

        describe("confidence adjustment") {
            beforeEachTest {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(120.0, 1))
            }

            context("when we hear beat updates at appropriate intervals") {
                beforeEachTest {
                    fakeClock.time += 2.0
                    beatSource.adjustConfidence()
                }

                it("retains high confidence") {
                    expect(beatSource.currentBeat.confidence).toBe(1f)
                }
            }

            context("when we haven't heard a beat update for more than a measure") {
                beforeEachTest {
                    fakeClock.time += 2.1
                    beatSource.adjustConfidence()
                }

                it("reduces the confidence") {
                    expect(beatSource.currentBeat.confidence).toBe(.99f)
                }
            }
        }

        context("waveforms") {
            val waveform by value { Waveform("074080bf1a111111") }

            it("#sampleCount") {
                expect(waveform.sampleCount).toBe(2)
            }

            it("#totalTimeMs") {
                expect(waveform.totalTimeMs).toBe(13)
            }

            it("encodes compactly") {
                expect(
                    Waveform.Builder().apply {
                        add(7, Color(0x40, 0x80, 0xbf))
                        add(26, Color(0x11, 0x11, 0x11))
                    }.build()
                ).toEqual(waveform)
            }

            it("decodes correctly") {
                expect(waveform.heightAt(0))
                    .toEqual(7)
                expect(waveform.colorAt(0))
                    .toEqual(baaahs.Color(0x40, 0x80, 0xbf))

                expect(waveform.heightAt(1))
                    .toEqual(26)
                expect(waveform.colorAt(1))
                    .toEqual(baaahs.Color(0x11, 0x11, 0x11))
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

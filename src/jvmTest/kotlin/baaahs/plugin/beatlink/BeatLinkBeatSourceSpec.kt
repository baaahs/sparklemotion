package baaahs.plugin.beatlink

import baaahs.Color
import baaahs.FakeClock
import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toBeWithErrorTolerance
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import org.deepsymmetry.beatlink.Beat
import org.deepsymmetry.beatlink.Util
import org.deepsymmetry.beatlink.data.TrackPositionUpdate
import org.deepsymmetry.beatlink.data.WaveformDetail
import org.spekframework.spek2.Spek
import kotlin.math.roundToInt
import kotlin.test.assertNotEquals
import kotlin.time.Duration.Companion.seconds

@Suppress("unused")
object BeatLinkBeatSourceSpec : Spek({
    describe<BeatLinkBeatSource> {
        val startTime by value { FakeClock(10.0).now() }
        val fakeClock by value { FakeClock(startTime) }
        val fakeTimeFinder by value { FakeTimeFinder() }
        val beatSource by value { BeatLinkBeatSource(fakeClock, fakeTimeFinder) }

        context("before any beats have been received") {
            it("current beat is zero") {
                expect(beatSource.currentBeat).toBe(BeatData(0.0, 0, confidence = 0f))
            }
        }

        context("when a new beat is received") {
            it("beat data is updated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 1))
                expect(beatSource.currentBeat).toBe(BeatData(10.0, 486, confidence = 1f))
            }
        }

        context("when a mid-bar new beat is received") {
            it("beat data is updated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 2))
                expect(beatSource.currentBeat).toBe(BeatData(10.0 - 60 / 123.4, 486, confidence = 1f))
            }
        }

        context("when a mid-bar new beat is received with a small measure start variance") {
            it("beat data is not updated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 1))

                fakeClock.time += 0.486.seconds
                beatSource.newBeat(mockBeat(123.4, 2))
                expect(beatSource.currentBeat).toBe(BeatData(10.0, 486, confidence = 1f))

                fakeClock.time += 0.490.seconds
                beatSource.newBeat(mockBeat(123.4, 3))
                expect(beatSource.currentBeat.measureStartTime).toBeWithErrorTolerance(10.003, 0.0009)
                expect(beatSource.currentBeat.beatIntervalMs).toBe(486)
            }
        }

        context("when a mid-bar new beat is received with a small measure start variance and a different tempo") {
            it("beat data is updated") {
                beatSource.channelsOnAir(hashSetOf(1))
                beatSource.newBeat(mockBeat(123.4, 1))

                fakeClock.time += 0.486.seconds
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
                    fakeClock.time += 2.0.seconds
                    beatSource.adjustConfidence()
                }

                it("retains high confidence") {
                    expect(beatSource.currentBeat.confidence).toBe(1f)
                }
            }

            context("when we haven't heard a beat update for more than a measure") {
                beforeEachTest {
                    fakeClock.time += 2.1.seconds
                    beatSource.adjustConfidence()
                }

                it("reduces the confidence") {
                    expect(beatSource.currentBeat.confidence).toBe(.99f)
                }
            }
        }

        fun setTrackPosition(deviceNumber: Int, positionInTrackMs: Long, beatNumber: Int, pitch: Double) {
            fakeTimeFinder.latestPositionFor[deviceNumber] = TrackPositionUpdate(
                fakeClock.time.toEpochMilliseconds(),
                positionInTrackMs, beatNumber, true, true, pitch, false, null
            )
        }

        fun sendWaveformDetail(deviceNumber: Int) {
            beatSource.onWaveformDetailChanged(deviceNumber, Waveform.Builder(deviceNumber).apply {
                add(7, Color(0x40, 0x80, 0xbf))
                add(26, Color(0x11, 0x11, 0x11))
            }.build().asDetail())
        }

        context("player state") {
            val state by value { BeatLinkState() }
            beforeEachTest { beatSource.addListener(state) }

            context("before any events have been received") {
                it("current beat and player state is unknown") {
                    expect(state.currentBeat).toBe(BeatData(0.0, 0, confidence = 0f))
                    expect(state.playerStates[1]).toBe(null)
                }
            }

            context("when a player comes on air") {
                beforeEachTest { beatSource.channelsOnAir(hashSetOf(1)) }

                it("shows the player as on air") {
                    expect(state.currentBeat).toBe(BeatData(0.0, 0, confidence = 0f))
                    expect(state.playerStates[1]).toBe(PlayerState(isOnAir = true))
                }

                context("when a beat arrives") {
                    beforeEachTest { beatSource.newBeat(mockBeat(120.0, 1)) }

                    it("updates the current beat") {
                        expect(state.currentBeat).toBe(BeatData(10.0, 500, confidence = 1f))
                    }

                    it("updates the player state") {
                        expect(state.playerStates[1]).toBe(PlayerState(isOnAir = true))
                    }
                }

                context("when we have waveform detail for a player") {
                    beforeEachTest {
                        setTrackPosition(1, 0, 0, 1.0)
                        sendWaveformDetail(1)
                    }

                    it("updates the current beat") {
                        expect(state.currentBeat).toBe(BeatData.unknown)
                    }

                    it("updates the player state") {
                        expect(state.playerStates[1])
                            .toEqual(PlayerState(encodedWaveform = "074080bf", waveformScale = 8, trackStartTime = startTime, isOnAir = true))
                    }

                    context("and beat 0 arrives") {
                        beforeEachTest {
                            setTrackPosition(1, 0, 0, 1.0)
                            beatSource.newBeat(mockBeat(120.0, 0))
                        }

                        it("updates the current beat") {
                            expect(state.currentBeat).toBe(BeatData(10.5, 500))
                        }

                        it("updates the player state") {
                            expect(state.playerStates[1])
                                .toEqual(PlayerState(encodedWaveform = "074080bf", waveformScale = 8, trackStartTime = startTime, isOnAir = true))
                        }

                        context("and then beat 1 arrives with normal tempo") {
                            beforeEachTest {
                                fakeClock.time += .5.seconds
                                setTrackPosition(1, 500, 1, 1.0)
                                beatSource.newBeat(mockBeat(120.0, 1))
                            }

                            it("updates the current beat") {
                                expect(state.currentBeat).toBe(BeatData(10.5, 500))
                            }

                            it("updates the player state") {
                                expect(state.playerStates[1])
                                    .toEqual(PlayerState(encodedWaveform = "074080bf", waveformScale = 8, trackStartTime = startTime, isOnAir = true))
                            }
                        }

                        context("and then beat 1 arrives with changed tempo") {
                            beforeEachTest {
                                fakeClock.time += .4.seconds
                                setTrackPosition(1, 500, 1, 1.25)
                                beatSource.newBeat(mockBeat(150.0, 1, isTempoMaster = false, pitch = 1.25))
                            }

                            it("updates the current beat") {
                                expect(state.currentBeat).toBe(BeatData(10.4, 400))
                            }

                            it("updates the player state") {
                                expect(state.playerStates[1])
                                    .toEqual(PlayerState(encodedWaveform = "074080bf", waveformScale = 8, trackStartTime = startTime, isOnAir = true))
                            }
                        }
                    }
                }

                context("when a different player comes on air") {
                    beforeEachTest { beatSource.channelsOnAir(hashSetOf(2)) }

                    it("shows the first player as off air") {
                        expect(state.playerStates[1]).toBe(PlayerState(isOnAir = false))
                    }
                }
            }

            context("when we have waveform detail for a player") {
                beforeEachTest {
                    setTrackPosition(1, 0, 0, 1.0)
                    sendWaveformDetail(1)
                }

                it("doesn't update the current beat, because we're not tempo master") {
                    expect(state.currentBeat).toBe(BeatData.unknown)
                }

                it("updates the player state") {
                    expect(state.playerStates[1])
                        .toEqual(PlayerState(encodedWaveform = "074080bf", waveformScale = 8, trackStartTime = startTime))
                }

                context("and a new beat arrives") {
                    beforeEachTest {
                        fakeClock.time += .5.seconds
                        setTrackPosition(1, 500, 1, 1.0)
                        beatSource.newBeat(mockBeat(120.0, 1, isTempoMaster = false))
                    }

                    it("doesn't update the current beat, because we're not tempo master") {
                        expect(state.currentBeat).toBe(BeatData.unknown)
                    }

                    it("updates the player state") {
                        expect(state.playerStates[1])
                            .toEqual(PlayerState(encodedWaveform = "074080bf", waveformScale = 8, trackStartTime = startTime))
                    }
                }
            }
        }

        context("waveforms") {
            val playerState by value { PlayerState(encodedWaveform = "074080bf1a111111", waveformScale = 1) }

            it("#sampleCount") {
                expect(playerState.waveform?.sampleCount).toBe(2)
            }

            it("#totalTimeMs") {
                expect(playerState.waveform?.totalTimeMs?.roundToInt()).toBe(13)
            }

            it("encodes compactly") {
                expect(
                    PlayerState().withWaveform(1) {
                        add(7, Color(0x40, 0x80, 0xbf))
                        add(26, Color(0x11, 0x11, 0x11))
                    }
                ).toEqual(playerState)
            }

            it("decodes correctly") {
                expect(playerState.waveform?.heightAt(0))
                    .toEqual(7)
                expect(playerState.waveform?.colorAt(0))
                    .toEqual(Color(0x40, 0x80, 0xbf))

                expect(playerState.waveform?.heightAt(1))
                    .toEqual(26)
                expect(playerState.waveform?.colorAt(1))
                    .toEqual(Color(0x11, 0x11, 0x11))
            }
        }
    }
})

class FakeTimeFinder : TimeFinder {
    val latestPositionFor = mutableMapOf<Int, TrackPositionUpdate>()

    override val isRunning: Boolean
        get() = true

    override fun start() {}

    override fun getLatestPositionFor(deviceNumber: Int): TrackPositionUpdate? =
        latestPositionFor[deviceNumber]

}

class BeatLinkState : BeatLinkListener {
    var currentBeat = BeatData.unknown
    val playerStates = mutableMapOf<Int, PlayerState>()

    override fun onBeatData(beatData: BeatData) {
        this.currentBeat = beatData
    }

    override fun onPlayerStateUpdate(deviceNumber: Int, playerState: PlayerState) {
        playerStates[deviceNumber] = playerState
    }
}

/** See [Util.pitchToMultiplier] for the pitch calculation. */
private const val pitchFactor = 0xFFFF7.toDouble()
private fun multiplierToPitch(pitch: Double) = (pitchFactor / pitch).roundToInt()

fun mockBeat(effectiveTempo: Double, beatWithinBar: Int, deviceNumber: Int = 1, pitch: Double = 1.0, isTempoMaster: Boolean = true): Beat = mockk {
    val beat = this
    every { beat.effectiveTempo } returns effectiveTempo
    every { beat.beatWithinBar } returns beatWithinBar
    every { beat.deviceNumber } returns deviceNumber
    every { beat.pitch } returns multiplierToPitch(pitch)
    every { beat.isTempoMaster } returns isTempoMaster
    every { deviceName } returns "Fake CDJ"
}

fun mockWaveformDetail(waveform: Waveform): WaveformDetail = mockk {
    val detail = this
    every { detail.frameCount } returns waveform.sampleCount
    val segmentCapture = CapturingSlot<Int>()
    val scaleCapture = CapturingSlot<Int>()
    every { detail.segmentHeight(capture(segmentCapture), capture(scaleCapture)) }
        .answers { waveform.heightAt(segmentCapture.captured / scaleCapture.captured) }
    every { detail.segmentColor(capture(segmentCapture), capture(scaleCapture)) }
        .answers { java.awt.Color(waveform.colorAt(segmentCapture.captured / scaleCapture.captured).argb) }
}

private fun Waveform.asDetail(): WaveformDetail = mockWaveformDetail(this)
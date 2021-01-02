package baaahs.visualizer.movers

import baaahs.FakeClock
import baaahs.TestMovingHead
import baaahs.describe
import baaahs.sim.FakeDmxUniverse
import baaahs.visualizer.VizScene
import org.spekframework.spek2.Spek

object VizMovingHeadSpec: Spek({
    describe<VizMovingHead> {
        val movingHead by value {
            TestMovingHead(
                panMotorSpeed = 2f,
                tiltMotorSpeed = 1f,
                colorWheelMotorSpeed = 1f
            )
        }
        val dmxUniverse by value { FakeDmxUniverse() }
        val fakeClock by value { FakeClock() }
        val beam by value { BeamForTest() }
        val vizMovingHead by value { VizMovingHead(movingHead, dmxUniverse, fakeClock, beam) }

        val sendDmxFrame by value<UpdateMoverState> {
            { elapsedTime, pan, tilt, colorWheelPosition, dimmer ->
                fakeClock.time += elapsedTime

                val buffer = movingHead.newBuffer(dmxUniverse)
                buffer.pan = pan
                buffer.tilt = tilt
                buffer.colorWheelPosition = colorWheelPosition
                buffer.dimmer = dimmer
                dmxUniverse.sendFrame()
            }
        }

        beforeEachTest { vizMovingHead.run {} }

        context("when a frame requests motor movement") {
            beforeEachTest {
                sendDmxFrame(1/16f, 1/4f, 0f, 0f, 1f)
            }

            it("doesn't move immediately") {
                beam.currentState.shouldRoughlyEqual(
                    State(0f, 0f, 0f, 1f)
                )
            }

            context("on the next frame") {
                beforeEachTest {
                    sendDmxFrame(1/16f, 1/4f, 0f, 0f, 1f)
                }

                it("moves as far as it could have moved in the time between frames") {
                    beam.currentState.shouldRoughlyEqual(
                        State(1/32f, 0f, 0f, 1f)
                    )
                }
            }

            context("then moving back") {
                beforeEachTest {
                    sendDmxFrame(1/16f, 0f, 0f, 0f, 1f)
                }

                it("moves as far as it could have moved in the time between frames") {
                    beam.currentState.shouldRoughlyEqual(
                        State(1/32f, 0f, 0f, 1f)
                    )
                }

                context("after another frame") {
                    beforeEachTest {
                        sendDmxFrame(1/16f, 0f, 0f, 0f, 1f)
                    }

                    it("moves as far as it could have moved in the time between frames") {
                        beam.currentState.shouldRoughlyEqual(
                            State(0f, 0f, 0f, 1f)
                        )
                    }
                }
            }
        }
    }
})

typealias UpdateMoverState = (
    elapsedTime: Float,
    pan: Float,
    tilt: Float,
    colorWheelPosition: Float,
    dimmer: Float
) -> Unit

class BeamForTest : Beam {
    var currentState = State()

    override fun addTo(scene: VizScene) {
    }

    override fun update(state: State) {
        currentState = state
    }
}
package baaahs.visualizer.movers

import baaahs.FakeClock
import baaahs.TestMovingHeadAdapter
import baaahs.describe
import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import baaahs.visualizer.VizObj
import org.spekframework.spek2.Spek

object VizMovingHeadSpec: Spek({
    describe<MovingHeadVisualizer> {
        val movingHead by value {
            MovingHead(
                "test", "Test", 1,
                TestMovingHeadAdapter(
                    panMotorSpeed = 2f,
                    tiltMotorSpeed = 1f,
                    colorWheelMotorSpeed = 1f
                ),
                Matrix4F.fromPositionAndRotation(Vector3F.origin, EulerAngle.identity)
            )
        }
        val dmxUniverse by value { FakeDmxUniverse() }
        val fakeClock by value { FakeClock() }
        val beam by value { BeamForTest() }
        val vizMovingHead by value { MovingHeadVisualizer(movingHead, fakeClock, dmxUniverse, beam) }

        val sendDmxFrame by value<UpdateMoverState> {
            { elapsedTime, pan, tilt, colorWheelPosition, dimmer ->
                fakeClock.time += elapsedTime

                val buffer = movingHead.adapter.newBuffer(dmxUniverse, 1)
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

    override fun addTo(scene: VizObj) {
    }

    override fun update(state: State) {
        currentState = state
    }
}
package baaahs.visualizer.movers

import baaahs.FakeClock
import baaahs.TestMovingHeadAdapter
import baaahs.describe
import baaahs.sim.FakeDmxUniverse
import org.spekframework.spek2.Spek
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object PhysicsModelSpec: Spek({
    describe<PhysicalModel> {
        val movingHeadAdapter by value {
            TestMovingHeadAdapter(
                panMotorSpeed = 2f,
                tiltMotorSpeed = 1f,
                colorWheelMotorSpeed = 1f,
                panRange = 0f..1f,
                tiltRange = 0f..1f
            )
        }
        val dmxUniverse by value { FakeDmxUniverse() }
        val fakeClock by value { FakeClock() }
        val beam by value { BeamForTest() }
        val physicalModel by value { PhysicalModel(movingHeadAdapter, fakeClock) }

        val sendDmxFrame by value<UpdateMoverState> {
            { elapsedTime, pan, tilt, colorWheelPosition, dimmer ->
                fakeClock.time += elapsedTime

                val buffer = movingHeadAdapter.newBuffer(dmxUniverse, 1)
                buffer.pan = pan
                buffer.tilt = tilt
                buffer.colorWheelPosition = colorWheelPosition
                buffer.dimmer = dimmer
                beam.currentState = physicalModel.update(buffer)
            }
        }

        beforeEachTest { physicalModel.run {} }

        context("when a frame requests motor movement") {
            beforeEachTest {
                sendDmxFrame((1.0/16).seconds, 1/4f, 0f, 0f, 1f)
            }

            it("doesn't move immediately") {
                beam.currentState.shouldRoughlyEqual(
                    State(0f, 0f, 0f, 1f)
                )
            }

            context("on the next frame") {
                beforeEachTest {
                    sendDmxFrame((1.0/16).seconds, 1/4f, 0f, 0f, 1f)
                }

                it("moves as far as it could have moved in the time between frames") {
                    beam.currentState.shouldRoughlyEqual(
                        State(1/32f, 0f, 0f, 1f)
                    )
                }
            }

            context("then moving back") {
                beforeEachTest {
                    sendDmxFrame((1.0/16).seconds, 0f, 0f, 0f, 1f)
                }

                it("moves as far as it could have moved in the time between frames") {
                    beam.currentState.shouldRoughlyEqual(
                        State(1/32f, 0f, 0f, 1f)
                    )
                }

                context("after another frame") {
                    beforeEachTest {
                        sendDmxFrame((1.0/16).seconds, 0f, 0f, 0f, 1f)
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
    elapsedTime: Duration,
    pan: Float,
    tilt: Float,
    colorWheelPosition: Float,
    dimmer: Float
) -> Unit

class BeamForTest(var currentState: State = State())
package baaahs.visualizer.movers

import baaahs.FakeClock
import baaahs.TestMovingHeadAdapter
import baaahs.describe
import baaahs.kotest.value
import baaahs.sim.FakeDmxUniverse
import io.kotest.core.spec.style.DescribeSpec
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object PhysicsModelSpec: DescribeSpec({
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

        beforeEach { physicalModel.run {} }

        context("when a frame requests motor movement") {
            beforeEach {
                sendDmxFrame((1.0/16).seconds, 1/4f, 0f, 0f, 1f)
            }

            it("doesn't move immediately") {
                beam.currentState.shouldRoughlyEqual(
                    State(0f, 0f, 0f, 1f)
                )
            }

            context("on the next frame") {
                beforeEach {
                    sendDmxFrame((1.0/16).seconds, 1/4f, 0f, 0f, 1f)
                }

                it("moves as far as it could have moved in the time between frames") {
                    beam.currentState.shouldRoughlyEqual(
                        State(1/32f, 0f, 0f, 1f)
                    )
                }
            }

            context("then moving back") {
                beforeEach {
                    sendDmxFrame((1.0/16).seconds, 0f, 0f, 0f, 1f)
                }

                it("moves as far as it could have moved in the time between frames") {
                    beam.currentState.shouldRoughlyEqual(
                        State(1/32f, 0f, 0f, 1f)
                    )
                }

                context("after another frame") {
                    beforeEach {
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
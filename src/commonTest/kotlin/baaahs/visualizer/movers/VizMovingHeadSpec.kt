package baaahs.visualizer.movers

import baaahs.FakeClock
import baaahs.describe
import baaahs.dmx.Shenzarpy
import baaahs.geom.Vector3F
import baaahs.sim.FakeDmxUniverse
import baaahs.toEqual
import baaahs.visualizer.VizScene
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import kotlin.math.absoluteValue

object VizMovingHeadSpec: Spek({
    describe<VizMovingHead> {
        val movingHead by value { Shenzarpy("fake", "Fake", 1, Vector3F.origin, Vector3F.origin) }
        val dmxUniverse by value { FakeDmxUniverse() }
        val fakeClock by value { FakeClock() }
        val beam by value { BeamForTest() }
        val vizMovingHead by value { VizMovingHead(movingHead, dmxUniverse, fakeClock, beam) }

        val sendDmxFrame by value<UpdateMoverState> {
            { pan, tilt, colorWheelPosition, dimmer ->
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
                fakeClock.time += 1/32f
                sendDmxFrame(1/128f, 0f, 0f, 1f)
            }

            it("doesn't move immediately") {
                beam.currentState.shouldRoughlyEqual(
                    State(1/128f, 0f, 0f, 1f)
                )
            }

            context("on the next frame") {
                beforeEachTest {
                    fakeClock.time += 1/32f
                    sendDmxFrame(1/128f, 0f, 0f, 1f)
                }

                it("moves as far as it could have moved in the time between frames") {
                    beam.currentState.shouldRoughlyEqual(
                        State(1/128f, 0f, 0f, 1f)
                    )
                }
            }
        }
    }
})

typealias UpdateMoverState = (
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

fun State.shouldRoughlyEqual(otherState: State, difference: Float = .0001f) {
    if (
        (pan - otherState.pan).absoluteValue < difference &&
        (tilt - otherState.tilt).absoluteValue < difference &&
        (colorWheelPosition - otherState.colorWheelPosition).absoluteValue < difference &&
        (dimmer - otherState.dimmer).absoluteValue < difference
    ) {
        // cool!
    } else {
        expect(this).toEqual(otherState)
    }
}
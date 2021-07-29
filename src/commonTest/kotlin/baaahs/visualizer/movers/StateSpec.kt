package baaahs.visualizer.movers

import baaahs.TestMovingHeadAdapter
import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import kotlin.math.absoluteValue

object StateSpec : Spek({
    describe<State> {
        val startingState by value { State() }
        val destinationState by value { State(.75f, .75f, .25f, 1f) }
        val adapter by value {
            TestMovingHeadAdapter(
                panMotorSpeed = 2f,
                tiltMotorSpeed = 1f,
                colorWheelMotorSpeed = 1f
            )
        }

        context("#moveToward") {
            val elapsedTime by value { .25f }
            val interpolatedState by value {
                startingState.moveToward(destinationState, destinationState, adapter, elapsedTime)
            }

            it("should move as far as motors can move in the elapsed time") {
                interpolatedState.shouldRoughlyEqual(
                    State(
                        pan = .125f,
                        tilt = .25f,
                        colorWheelPosition = .25f,
                        dimmer = 1f
                    )
                )
            }
        }
    }
})

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
package baaahs.visualizer.movers

import baaahs.Color
import baaahs.model.MovingHeadAdapter
import kotlin.math.absoluteValue
import kotlin.math.min

data class State(
    val pan: Float = 0f,
    val tilt: Float = 0f,
    val colorWheelPosition: Float = 0f,
    val dimmer: Float = 1f,
    val color: Color = Color.BLACK
) {
    fun moveToward(momentumState: State, requestedState: State, adapter: MovingHeadAdapter, elapsed: Float): State {
        return State(
            move(pan, momentumState.pan, adapter.panMotorSpeed, adapter.panRange, elapsed),
            move(tilt, momentumState.tilt, adapter.tiltMotorSpeed, adapter.tiltRange, elapsed),
            // TODO: The color wheel can spin freely so we should pick the shortest path (e.g. from .9 -> .1).
            move(colorWheelPosition, momentumState.colorWheelPosition, adapter.colorWheelMotorSpeed, 0f..1f, elapsed),
            requestedState.dimmer,
            requestedState.color
        )
    }

    fun move(startingPoint: Float, destination: Float, motorSpeed: Float, range: ClosedRange<Float>, elapsed: Float): Float {
        if (destination == startingPoint) return startingPoint

        val maxDist = elapsed / motorSpeed * range.diff
        val targetDist = destination - startingPoint
        val dist = min(targetDist.absoluteValue, maxDist)
        return startingPoint + if (destination < startingPoint) -dist else dist
    }
}
package baaahs.visualizer.movers

import baaahs.Color
import baaahs.model.MovingHead
import kotlin.math.absoluteValue
import kotlin.math.min

data class State(
    val pan: Float = 0f,
    val tilt: Float = 0f,
    val colorWheelPosition: Float = 0f,
    val dimmer: Float = 1f,
    val color: Color = Color.BLACK
) {
    fun moveToward(momentumState: State, requestedState: State, movingHead: MovingHead, elapsed: Float): State {
        return State(
            move(pan, momentumState.pan, movingHead.panMotorSpeed, elapsed),
            move(tilt, momentumState.tilt, movingHead.tiltMotorSpeed, elapsed),
            // TODO: The color wheel can spin freely so we should pick the shortest path (e.g. from .9 -> .1).
            move(colorWheelPosition, momentumState.colorWheelPosition, movingHead.colorWheelMotorSpeed, elapsed),
            requestedState.dimmer,
            requestedState.color
        )
    }

    fun move(startingPoint: Float, destination: Float, motorSpeed: Float, elapsed: Float): Float {
        if (destination == startingPoint) return startingPoint

        val maxDist = elapsed / motorSpeed
        val targetDist = destination - startingPoint
        val dist = min(targetDist.absoluteValue, maxDist)
        return startingPoint + if (destination < startingPoint) -dist else dist
    }
}
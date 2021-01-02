package baaahs.visualizer.movers

import baaahs.Color
import baaahs.model.MovingHead
import kotlin.math.absoluteValue
import kotlin.math.max

data class State(
    val pan: Float = 0f,
    val tilt: Float = 0f,
    val colorWheelPosition: Float = 0f,
    val dimmer: Float = 1f,
    val color: Color = Color.BLACK
) {
    fun moveToward(targetState: State, requestedState: State, movingHead: MovingHead, elapsed: Float): State {
        return State(
            pan + maxDist(targetState.pan - pan, movingHead.panRange, movingHead.panMotorSpeed, elapsed),
            tilt + maxDist(targetState.tilt - tilt, movingHead.tiltRange, movingHead.tiltMotorSpeed, elapsed),
            colorWheelPosition + maxDist(
                targetState.colorWheelPosition - colorWheelPosition,
                0f..1f,
                movingHead.colorWheelMotorSpeed,
                elapsed
            ),
            requestedState.dimmer,
            requestedState.color
        )
    }

    fun maxDist(targetDist: Float, range: ClosedRange<Float>, motorSpeed: Float, elapsed: Float): Float {
        val maxDist = range.diff * elapsed / motorSpeed
        val dist = max(targetDist.absoluteValue, maxDist)
        return if (targetDist < 0) -dist else dist
    }
}
package baaahs.visualizer.movers

import baaahs.model.MovingHead
import baaahs.model.MovingHeadAdapter
import baaahs.util.Clock
import baaahs.util.asDoubleSeconds

class PhysicalModel(
    private val adapter: MovingHeadAdapter,
    private val clock: Clock
) {
    var currentState = State()
        private set
    private var lastUpdate = clock.now().asDoubleSeconds
    var momentumState = State()
        private set

    fun update(buffer: MovingHead.Buffer): State {
        val now = clock.now().asDoubleSeconds
        val elapsed = (now - lastUpdate).toFloat()

        val requestedState = State(
            buffer.pan,
            buffer.tilt,
            buffer.colorWheelPosition,
            buffer.dimmer
        )

        val attainableState = currentState.moveToward(momentumState, requestedState, adapter, elapsed)
        lastUpdate = now
        currentState = attainableState
        momentumState = requestedState

        return attainableState
    }
}
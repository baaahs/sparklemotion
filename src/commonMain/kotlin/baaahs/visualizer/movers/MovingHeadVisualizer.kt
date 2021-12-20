package baaahs.visualizer.movers

import baaahs.Color
import baaahs.geom.Matrix4F
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.model.MovingHeadAdapter
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Clock
import baaahs.visualizer.EntityVisualizer
import baaahs.visualizer.VizScene
import kotlin.math.absoluteValue

class MovingHeadVisualizer(
    private val movingHead: MovingHead,
    private val clock: Clock,
    private val dmxUniverse: FakeDmxUniverse,
    private val beam: Beam = Beam.selectFor(movingHead)
) : EntityVisualizer {
    override val entity: Model.Entity get() = movingHead
    override val title: String
        get() = movingHead.name
    override var mapperIsRunning: Boolean = false
    override var selected: Boolean = false // TODO: Show that the device is selected.

    override var transformation: Matrix4F = Matrix4F.identity

    private val buffer = run {
        val dmxBufferReader = dmxUniverse.listen(movingHead.baseDmxChannel, movingHead.adapter.dmxChannelCount) {
            receivedDmxFrame()
        }
        movingHead.adapter.newBuffer(dmxBufferReader)
    }

    private var lastUpdate = clock.now()
    private var currentState = State()
    private var momentumState = State()

    override fun addTo(scene: VizScene) {
        beam.addTo(scene)
    }

    internal fun receivedDmxFrame() {
        val now = clock.now()
        val elapsed = (now - lastUpdate).toFloat()

        val requestedState = State(
            buffer.pan,
            buffer.tilt,
            buffer.colorWheelPosition,
            buffer.dimmer
        )

        val attainableState = currentState.moveToward(momentumState, requestedState, movingHead.adapter, elapsed)
        beam.update(attainableState)

        lastUpdate = now
        currentState = attainableState
        momentumState = requestedState
    }
}

enum class ColorMode(
    val isClipped: Boolean,
    val getColor: MovingHead.(State) -> Color
) {
    Rgb(false, { state -> state.color }),
    Primary(true, { state -> this.adapter.colorAtPosition(state.colorWheelPosition) }),
    Secondary(true, { state -> this.adapter.colorAtPosition(state.colorWheelPosition, next = true) })
}

fun ClosedRange<Float>.scale(value: Float) =
    (endInclusive - start) * value + start

val ClosedRange<Float>.diff
    get() =
        (endInclusive - start).absoluteValue

fun MovingHeadAdapter.colorAtPosition(position: Float, next: Boolean = false): Color {
    var colorIndex = (position.absoluteValue % 1f * colorWheelColors.size).toInt()
    if (next) colorIndex = (colorIndex + 1) % colorWheelColors.size
    return colorWheelColors[colorIndex].color
}

expect class Cone(movingHead: MovingHead, colorMode: ColorMode = ColorMode.Rgb) {
    fun addTo(scene: VizScene)

    fun update(state: State)
}
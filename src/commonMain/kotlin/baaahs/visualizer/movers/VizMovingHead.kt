package baaahs.visualizer.movers

import baaahs.Color
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Clock
import baaahs.visualizer.VizScene
import kotlin.math.absoluteValue

class VizMovingHead(
    private val movingHead: MovingHead,
    dmxUniverse: FakeDmxUniverse,
    private val clock: Clock
) {
    private val buffer = run {
        val dmxBufferReader = dmxUniverse.reader(movingHead.baseDmxChannel, movingHead.dmxChannelCount) {
            receivedDmxFrame()
        }
        movingHead.newBuffer(dmxBufferReader)
    }

    private val beam = when (movingHead.colorModel) {
        MovingHead.ColorModel.ColorWheel -> ColorWheelBeam(movingHead)
        MovingHead.ColorModel.RGB -> RgbBeam(movingHead)
        MovingHead.ColorModel.RGBW -> RgbBeam(movingHead)
    }

    private var lastUpdate = clock.now()
    private var currentState = State()
    private var targetState = State()

    fun addTo(scene: VizScene) {
        beam.addTo(scene)
    }

    private fun receivedDmxFrame() {
//        val now = clock.now()
//        val elapsed = (now - lastUpdate).toFloat()

        val requestedState = State(
            buffer.pan,
            buffer.tilt,
            buffer.colorWheelPosition,
            buffer.dimmer
        )
        beam.update(requestedState)

//        val attainableState = currentState.moveToward(targetState, requestedState, movingHead, elapsed)
//
//        lastUpdate = now
//        currentState = attainableState
//        targetState = requestedState
    }
}

enum class ColorMode(
    val isClipped: Boolean,
    val getColor: MovingHead.(State) -> Color
) {
    Rgb(false, { state -> state.color }),
    Primary(true, { state -> this.colorAtPosition(state.colorWheelPosition) }),
    Secondary(true, { state -> this.colorAtPosition(state.colorWheelPosition, next = true) })
}

fun ClosedRange<Float>.scale(value: Float) =
    (endInclusive - start) * value + start

val ClosedRange<Float>.diff
    get() =
        (endInclusive - start).absoluteValue

fun MovingHead.colorAtPosition(position: Float, next: Boolean = false): Color {
    var colorIndex = (position.absoluteValue % 1f * colorWheelColors.size).toInt()
    if (next) colorIndex = (colorIndex + 1) % colorWheelColors.size
    return colorWheelColors[colorIndex].color
}

expect class Cone(movingHead: MovingHead, colorMode: ColorMode = ColorMode.Rgb) {
    fun addTo(scene: VizScene)

    fun update(state: State)
}
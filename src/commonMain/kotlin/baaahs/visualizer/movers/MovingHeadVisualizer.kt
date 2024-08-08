package baaahs.visualizer.movers

import baaahs.Color
import baaahs.model.ModelUnit
import baaahs.model.MovingHeadAdapter
import baaahs.visualizer.VizObj
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

enum class ColorMode(
    val isClipped: Boolean,
    val getColor: MovingHeadAdapter.(State) -> Color
) {
    Rgb(false, { state -> state.color }),
    Primary(true, { state -> this.colorAtPosition(state.colorWheelPosition) }),
    Secondary(true, { state -> this.colorAtPosition(state.colorWheelPosition, next = true) })
}

fun ClosedRange<Float>.scale(value: Float) =
    (endInclusive - start) * value + start

fun ClosedRange<Float>.unscale(value: Float) =
    (value - start) / (endInclusive - start)

fun ClosedRange<Float>.clamp(value: Float) =
    max(min(value, endInclusive), start)

val ClosedRange<Float>.diff
    get() =
        (endInclusive - start).absoluteValue

fun MovingHeadAdapter.colorAtPosition(position: Float, next: Boolean = false): Color {
    var colorIndex = (position.absoluteValue % 1f * colorWheelColors.size).toInt()
    if (next) colorIndex = (colorIndex + 1) % colorWheelColors.size
    return colorWheelColors[colorIndex].color
}

expect class Cone(
    movingHeadAdapter: MovingHeadAdapter,
    units: ModelUnit,
    colorMode: ColorMode = ColorMode.Rgb
) {
    fun addTo(parent: VizObj)

    fun update(state: State)
}
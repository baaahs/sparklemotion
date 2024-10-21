package baaahs.ui.slider

import react.dom.events.KeyboardEvent
import react.dom.events.PointerEvent
import web.dom.Element
import kotlin.math.max
import kotlin.math.min

typealias EmitKeyboard = (e: KeyboardEvent<Element>, id: String) -> Unit
typealias EmitPointer = (e: PointerEvent<Element>, location: Location, handleId: String?) -> Unit

enum class Location {
    Handle, Rail, Track, Tick
}

fun getNextValue(
    curr: Double,
    step: Double,
    domain: Range,
    reversed: Boolean
): Double {
    val newVal = if (reversed) curr - step else curr + step
    return if (reversed) max(domain.start, newVal) else min(domain.endInclusive, newVal)
}

fun getPrevValue(
    curr: Double,
    step: Double,
    domain: Range,
    reversed: Boolean
): Double {
    val newVal = if (reversed) curr + step else curr - step
    return if (reversed) min(domain.endInclusive, newVal) else max(domain.start, newVal)
}

fun getSliderDomain(slider: Element?, vertical: Boolean): Range {
    if (slider == null) {
        return 0.0..0.0
    }

    val s = slider.getBoundingClientRect()

    val d0 = if (vertical) s.top else s.left
    val d1 = if (vertical) s.bottom else s.right

    return d0..d1
}
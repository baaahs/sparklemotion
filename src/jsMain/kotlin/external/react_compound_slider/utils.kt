package external.react_compound_slider

import react.ReactNode
import web.dom.Element
import kotlin.math.max
import kotlin.math.min

fun isRCSComponent(item: ReactNode): Boolean {
    if (!react.isValidElement(item)) return false
    val type = item.asDynamic().type
    val name = type?.displayName ?: type?.name ?: ""
    return (name == "Rail" || name == "BetterRail" ||
            name == "Handles" || name == "BetterHandles" ||
            name == "Ticks" || name == "BetterTicks" ||
            name == "Tracks" || name == "BetterTracks"
            )
}

fun getNextValue(
    curr: Double,
    step: Double,
    domain: Array<Double>,
    reversed: Boolean
): Double {
    val newVal = if (reversed) curr - step else curr + step
    return if (reversed) max(domain[0], newVal) else min(domain[1], newVal)
}

fun getPrevValue(
    curr: Double,
    step: Double,
    domain: Array<Double>,
    reversed: Boolean
): Double {
    val newVal = if (reversed) curr + step else curr - step
    return if (reversed) min(domain[1], newVal) else max(domain[0], newVal)
}

fun getSliderDomain(slider: Element?, vertical: Boolean): Range {
    if (slider == null) {
        return arrayOf(0.0, 0.0)
    }

    val s = slider.getBoundingClientRect()

    val d0 = if (vertical) s.top else s.left
    val d1 = if (vertical) s.bottom else s.right

    return arrayOf(d0, d1)
}

fun getTouchPosition(vertical: Boolean, e: web.uievents.TouchEvent): Double =
    if (vertical) e.touches[0].clientY else e.touches[0].pageX
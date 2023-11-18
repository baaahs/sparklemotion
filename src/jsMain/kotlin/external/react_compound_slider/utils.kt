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

fun Range.interpolateValue(t: Double): Double {
    return start + spread() * t
}

fun Range.deinterpolateValue(t: Double): Double {
    val spread = spread()
    return if (spread == 0.0) spread else (t - start) / spread
}

fun Range.properlyOrdered() : Range =
    reversedIf(start > endInclusive)

fun Range.clamp(x: Double): Double =
    x.coerceIn(start, endInclusive)

fun Range.spread(): Double =
    endInclusive - start

fun Range.reversedIf(reversed: Boolean): Range =
    if (reversed) endInclusive..start else this

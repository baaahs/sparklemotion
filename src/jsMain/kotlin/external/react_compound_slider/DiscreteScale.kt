package external.react_compound_slider

import kotlin.math.round

class DiscreteScale(
    val step: Double? = null,
    var domain: Range = 0.0..1.0,
    val range: Range = 0.0..1.0,
    val reversed: Boolean = false
) {
    fun getValue(x: Double): Double {
        val maybeReversedDomain = domain // .reversedIf(reversed)
        val p = (x - maybeReversedDomain.start) / maybeReversedDomain.spread()
        val maybeReversedRange = range.reversedIf(reversed)
        val b = if (step != null) {
            step * round((p * (maybeReversedRange.spread())) / step) + maybeReversedRange.start
        } else p * (maybeReversedRange.spread()) + maybeReversedRange.start
        return b
    }
}

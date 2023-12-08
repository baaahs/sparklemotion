package baaahs.ui.slider

import external.ticks

class LinearScale(
    val domain: Range = 0.0..1.0,
    val range: Range = 0.0..1.0,
    val reversed: Boolean = false
) {
    fun getValue(x: Double, clamp: Boolean = false): Double =
        range.reversedIf(reversed)
            .interpolateValue(domain.deinterpolateValue(x))
            .let { if (clamp) it.coerceIn(range) else it }

    fun getTicks(count: Int?): Array<Double> =
        ticks(domain.start, domain.endInclusive, count ?: 10)
}
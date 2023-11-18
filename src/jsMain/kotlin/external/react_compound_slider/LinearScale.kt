package external.react_compound_slider

import external.ticks

class LinearScale(
    val domain: Range = 0.0..1.0,
    val range: Range = 0.0..1.0,
    val reversed: Boolean = false
) {
    fun getValue(x: Double): Double =
        range.reversedIf(reversed)
            .interpolateValue(domain.deinterpolateValue(x))

    fun getTicks(count: Int?): Array<Double> =
        ticks(domain.start, domain.endInclusive, count ?: 10)
}
package baaahs.ui.slider

typealias Range = ClosedFloatingPointRange<Double>

fun Range.interpolateValue(t: Double): Double =
    start + spread() * t

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
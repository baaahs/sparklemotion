package baaahs.util

import kotlin.math.PI

private val twoPi = 2 * PI

/**
 * @param rad Radians in [-∞, ∞] range.
 * @return Radians in (-π, π] range.
 */
fun normalizeRad(rad: Double): Double =
    ((rad % twoPi + twoPi) % twoPi)
        .let { if (it <= PI) it else it - twoPi }


/**
 * @param rad Radians in [-∞, ∞] range.
 * @return Degrees in (-180, 180] range.
 */
fun rad2deg(rad: Float): Float = rad2deg(rad.toDouble()).toFloat()

/**
 * @param rad Radians in [-∞, ∞] range.
 * @return Degrees in (-180, 180] range.
 */
fun rad2deg(rad: Double): Double = normalizeRad(rad) / twoPi * 360

/**
 * @param deg Degrees in [-∞, ∞] range.
 * @return Degrees in (-180, 180] range.
 */
fun normalizeDeg(deg: Double): Double =
    ((deg % 360 + 360) % 360)
        .let { if (it <= 180) it else it - 360 }

/**
 * @param deg Degrees in [-∞, ∞] range.
 * @return Radians in (-π, π] range.
*/
fun deg2rad(deg: Float): Float = deg2rad(deg.toDouble()).toFloat()

/**
 * @param deg Degrees in [-∞, ∞] range.
 * @return Radians in (-π, π] range.
 */
fun deg2rad(deg: Double): Double = normalizeDeg(deg) / 360 * twoPi
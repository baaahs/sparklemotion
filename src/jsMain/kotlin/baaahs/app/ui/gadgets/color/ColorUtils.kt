package baaahs.app.ui.gadgets.color

import baaahs.Color
import baaahs.geom.Vector2F
import kotlin.math.*

data class Polar(
    val r: Float,
    val phi: Float
) {
    fun toXy(): Vector2F {
        val x = cos(phi) * r
        val y = sin(phi) * r
        return Vector2F(x, y)
    }
}

fun Vector2F.toPolar(): Polar {
    val r = sqrt(x * x + y * y)
    val phi = atan2(y, x)
    return Polar(r, phi)
}


// rad in [-π, π] range
// return degree in [0, 1] range
fun rad2deg(rad: Float): Float {
    return ((rad + PI) / (2 * PI)).toFloat()
}

// degree in [0, 1] range
// return rad in [-π, π] range
fun deg2rad(deg: Float): Float {
    return ((deg * (2 * PI)) - PI).toFloat()
}

fun Polar.toColor(radius: Float, alpha: Float = 1f): Color {
    return Color.HSB(
        hue = rad2deg(phi),
        saturation = r / radius,
        brightness = 1.0f
    ).toRGB(alpha)
}

fun Color.toXy(radius: Float): Vector2F {
    // Convert the color to polar coordinates
    val hsb = toHSB()
    val deg = if (hsb.hue.isNaN()) 0f else hsb.hue; // hue will be NaN for white :-/
    val phi = deg2rad(deg)
    val r = hsb.saturation * radius
    return Polar(r, phi).toXy()
}

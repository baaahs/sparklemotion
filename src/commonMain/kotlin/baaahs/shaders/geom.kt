package baaahs.shaders

import kotlin.math.sqrt

class Vector2(val x: Float, val y: Float) {
    operator fun div(scalar: Float): Vector2 = Vector2(x / scalar, y / scalar)
    operator fun minus(scalar: Float): Vector2 = Vector2(x - scalar, y - scalar)
    operator fun minus(other: Vector2): Vector2 = Vector2(x - other.x, y - other.y)

    val length: Float
        get() = sqrt(x * x + y * y)
}

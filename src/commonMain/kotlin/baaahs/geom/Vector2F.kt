package baaahs.geom

import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min

@Serializable
data class Vector2F(val x: Float, val y: Float) {
    operator fun plus(other: Vector2F): Vector2F = Vector2F(x + other.x, y + other.y)

    operator fun minus(other: Vector2F): Vector2F = Vector2F(x - other.x, y - other.y)

    operator fun times(scalar: Float): Vector2F = Vector2F(x * scalar, y * scalar)

    operator fun times(other: Vector2F): Vector2F = Vector2F(x * other.x, y * other.y)

    operator fun div(scalar: Float): Vector2F = Vector2F(x / scalar, y / scalar)

    operator fun div(other: Vector2F): Vector2F = Vector2F(x / other.x, y / other.y)

    operator fun unaryMinus(): Vector2F = Vector2F(-x, -y)

    fun clamp(minValue: Vector2F, maxValue: Vector2F): Vector2F {
        return Vector2F(
            max(min(x, maxValue.x), minValue.x),
            max(min(y, maxValue.y), minValue.y)
        )
    }

    override fun toString(): String = "Vector2F(x=$x, y=$y)"

    companion object {
        val origin = Vector2F(0f, 0f)
        val unit2d = Vector2F(1f, 1f)
    }
}
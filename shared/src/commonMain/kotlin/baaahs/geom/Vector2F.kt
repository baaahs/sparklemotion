package baaahs.geom

import baaahs.clamp
import kotlinx.serialization.Serializable

@Serializable
data class Vector2F(val x: Float, val y: Float) {
    constructor(x: Double, y: Double): this(x.toFloat(), y.toFloat())

    operator fun plus(other: Vector2F): Vector2F = Vector2F(x + other.x, y + other.y)

    operator fun minus(other: Vector2F): Vector2F = Vector2F(x - other.x, y - other.y)

    operator fun times(scalar: Float): Vector2F = Vector2F(x * scalar, y * scalar)

    operator fun times(other: Vector2F): Vector2F = Vector2F(x * other.x, y * other.y)

    operator fun div(scalar: Float): Vector2F = Vector2F(x / scalar, y / scalar)

    operator fun div(other: Vector2F): Vector2F = Vector2F(x / other.x, y / other.y)

    operator fun unaryMinus(): Vector2F = Vector2F(-x, -y)

    fun clamp(minValue: Vector2F, maxValue: Vector2F): Vector2F =
        Vector2F(
            x.clamp(minValue.x, maxValue.x),
            y.clamp(minValue.y, maxValue.y)
        )

    fun abs(): Vector2F = Vector2F(kotlin.math.abs(x), kotlin.math.abs(y))

    override fun toString(): String = "Vector2F(x=$x, y=$y)"

    companion object {
        val origin = Vector2F(0f, 0f)
        val unit2d = Vector2F(1f, 1f)
    }
}
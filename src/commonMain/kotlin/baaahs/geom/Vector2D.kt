package baaahs.geom

import baaahs.clamp
import kotlinx.serialization.Serializable

@Serializable
data class Vector2D(val x: Double, val y: Double) {
    constructor(x: Number, y: Number): this(x.toDouble(), y.toDouble())

    operator fun plus(other: Vector2D): Vector2D = Vector2D(x + other.x, y + other.y)

    operator fun minus(other: Vector2D): Vector2D = Vector2D(x - other.x, y - other.y)

    operator fun times(scalar: Double): Vector2D = Vector2D(x * scalar, y * scalar)

    operator fun times(other: Vector2D): Vector2D = Vector2D(x * other.x, y * other.y)

    operator fun div(scalar: Double): Vector2D = Vector2D(x / scalar, y / scalar)

    operator fun div(other: Vector2D): Vector2D = Vector2D(x / other.x, y / other.y)

    operator fun unaryMinus(): Vector2D = Vector2D(-x, -y)

    fun clamp(minValue: Vector2D, maxValue: Vector2D): Vector2D =
        Vector2D(
            x.clamp(minValue.x, maxValue.x),
            y.clamp(minValue.y, maxValue.y)
        )

    override fun toString(): String = "Vector2F(x=$x, y=$y)"

    companion object {
        val origin = Vector2D(0.0, 0.0)
        val unit2d = Vector2D(1.0, 1.0)
    }
}
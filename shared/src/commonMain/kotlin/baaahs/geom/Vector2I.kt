package baaahs.geom

import baaahs.clamp
import kotlinx.serialization.Serializable

@Serializable
data class Vector2I(val x: Int, val y: Int) {
    constructor(x: Number, y: Number): this(x.toInt(), y.toInt())

    operator fun plus(other: Vector2I): Vector2I = Vector2I(x + other.x, y + other.y)

    operator fun minus(other: Vector2I): Vector2I = Vector2I(x - other.x, y - other.y)

    operator fun times(scalar: Int): Vector2I = Vector2I(x * scalar, y * scalar)

    operator fun times(scalar: Double): Vector2I = Vector2I(x * scalar, y * scalar)

    operator fun times(other: Vector2I): Vector2I = Vector2I(x * other.x, y * other.y)

    operator fun div(scalar: Int): Vector2I = Vector2I(x / scalar, y / scalar)

    operator fun div(other: Vector2I): Vector2I = Vector2I(x / other.x, y / other.y)

    operator fun unaryMinus(): Vector2I = Vector2I(-x, -y)

    fun clamp(
        minValue: Vector2I = this,
        maxValue: Vector2I = this
    ): Vector2I =
        Vector2I(
            x.clamp(minValue.x, maxValue.x),
            y.clamp(minValue.y, maxValue.y)
        )

    override fun toString(): String = "Vector2F(x=$x, y=$y)"

    companion object {
        val origin = Vector2I(0, 0)
        val unit2d = Vector2I(1, 1)
    }
}
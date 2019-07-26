package baaahs.geom

import kotlinx.serialization.Serializable

@Serializable
class Vector2F(val x: Float, val y: Float) {
    operator fun component1() = x
    operator fun component2() = y
}
package baaahs.geom

import kotlinx.serialization.Serializable

@Serializable
class Vector4F(val x: Float, val y: Float, val z: Float, val w: Float) {
    operator fun component1() = x
    operator fun component2() = y
    operator fun component3() = z
    operator fun component4() = w

    override fun toString(): String = "Vector4F(x=$x, y=$y, z=$z, w=$w)"
}
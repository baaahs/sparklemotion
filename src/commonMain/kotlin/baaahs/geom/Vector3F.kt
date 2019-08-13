package baaahs.geom

import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class Vector3F(val x: Float, val y: Float, val z: Float) {
    fun min(other: Vector3F): Vector3F = Vector3F(min(x, other.x), min(y, other.y), min(z, other.z))

    fun max(other: Vector3F): Vector3F = Vector3F(min(x, other.x), min(y, other.y), min(z, other.z))

    fun plus(other: Vector3F): Vector3F = Vector3F(x + other.x, y + other.y, z + other.z)

    fun minus(other: Vector3F): Vector3F = Vector3F(x - other.x, y - other.y, z - other.z)

    fun times(scalar: Float): Vector3F = Vector3F(x * scalar, y * scalar, z * scalar)
}

fun center(vectors: Collection<Vector3F>): Vector3F {
    val min = vectors.reduce { acc, vector3F -> acc.min(vector3F) }
    val max = vectors.reduce { acc, vector3F -> acc.max(vector3F) }
    val diff = max.minus(min)
    return diff.times(0.5f).plus(min)
}
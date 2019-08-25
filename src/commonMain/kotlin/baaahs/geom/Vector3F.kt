package baaahs.geom

import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@Serializable
data class Vector3F(val x: Float, val y: Float, val z: Float) {
    fun min(other: Vector3F): Vector3F = Vector3F(min(x, other.x), min(y, other.y), min(z, other.z))

    fun max(other: Vector3F): Vector3F = Vector3F(max(x, other.x), max(y, other.y), max(z, other.z))

    fun plus(other: Vector3F): Vector3F = Vector3F(x + other.x, y + other.y, z + other.z)

    fun minus(other: Vector3F): Vector3F = Vector3F(x - other.x, y - other.y, z - other.z)

    fun times(scalar: Float): Vector3F = Vector3F(x * scalar, y * scalar, z * scalar)

    fun normalize(): Vector3F {
        val invLength = 1.0f / length()
        return Vector3F(x * invLength, y * invLength, z * invLength)
    }

    fun length(): Float {
        return sqrt(lengthSquared().toDouble()).toFloat()
    }

    private fun lengthSquared(): Float {
        return x * x + y * y + z * z
    }

    fun dividedByScalar(scalar: Float): Vector3F {
        return Vector3F(x / scalar, y / scalar, z / scalar)
    }

}

fun center(vectors: Collection<Vector3F>): Vector3F {
    val (min, max) = boundingBox(vectors)
    val diff = max.minus(min)
    return diff.times(0.5f).plus(min)
}

fun extents(vectors: Collection<Vector3F>): Vector3F {
    val (min, max) = boundingBox(vectors)
    return max.minus(min)
}

private fun boundingBox(vectors: Collection<Vector3F>): Pair<Vector3F, Vector3F> {
    val min = vectors.reduce { acc, vector3F -> acc.min(vector3F) }
    val max = vectors.reduce { acc, vector3F -> acc.max(vector3F) }
    return Pair(min, max)
}
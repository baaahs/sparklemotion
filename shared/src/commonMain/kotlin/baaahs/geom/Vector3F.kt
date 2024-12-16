package baaahs.geom

import baaahs.clamp
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@Serializable
data class Vector3F(val x: Float, val y: Float, val z: Float) {
    constructor(): this(0f, 0f, 0f)
    constructor(x: Double, y: Double, z: Double): this(x.toFloat(), y.toFloat(), z.toFloat())

    fun min(other: Vector3F): Vector3F = Vector3F(min(x, other.x), min(y, other.y), min(z, other.z))

    fun max(other: Vector3F): Vector3F = Vector3F(max(x, other.x), max(y, other.y), max(z, other.z))

    operator fun plus(other: Vector3F): Vector3F = Vector3F(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Vector3F): Vector3F = Vector3F(x - other.x, y - other.y, z - other.z)

    operator fun times(scalar: Float): Vector3F = Vector3F(x * scalar, y * scalar, z * scalar)

    operator fun times(scalar: Double): Vector3F = Vector3F(x * scalar, y * scalar, z * scalar)

    operator fun times(other: Vector3F): Vector3F = Vector3F(x * other.x, y * other.y, z * other.z)

    operator fun div(scalar: Double): Vector3F = Vector3F(x / scalar, y / scalar, z / scalar)

    operator fun div(scalar: Float): Vector3F = Vector3F(x / scalar, y / scalar, z / scalar)

    operator fun div(other: Vector3F): Vector3F = Vector3F(x / other.x, y / other.y, z / other.z)

    operator fun unaryMinus(): Vector3F = Vector3F(-x, -y, -z)

    fun clamp(minValue: Vector3F, maxValue: Vector3F): Vector3F =
        Vector3F(
            x.clamp(minValue.x, maxValue.x),
            y.clamp(minValue.y, maxValue.y),
            z.clamp(minValue.z, maxValue.z)
        )

    fun normalize(): Vector3F {
        val invLength = 1.0f / length()
        return Vector3F(x * invLength, y * invLength, z * invLength)
    }

    fun abs(): Vector3F {
        return Vector3F(abs(x), abs(y), abs(z))
    }

    fun dot(other: Vector3F): Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun length(): Float {
        return sqrt(lengthSquared().toDouble().absoluteValue).toFloat()
    }

    fun isNaN(): Boolean {
        return x.isNaN() || y.isNaN() || z.isNaN()
    }

    fun isZero(): Boolean {
        return x == 0f && y == 0f && z == 0f
    }

    fun isNonZero(): Boolean {
        return !isZero()
    }

    private fun lengthSquared(): Float {
        return x * x + y * y + z * z
    }

    fun transform(transformation: Matrix4F): Vector3F {
        return transformation.transform(this)
    }

    fun serialize(writer: ByteArrayWriter) {
        writer.writeFloat(x)
        writer.writeFloat(y)
        writer.writeFloat(z)
    }

    companion object {
        val origin = Vector3F(0f, 0f, 0f)
        val unit3d = Vector3F(1f, 1f, 1f)
        val unitNormal = unit3d.normalize()
        val facingForward = Vector3F(0f, 0f, 1f)
        val unknown = Vector3F(Float.NaN, Float.NaN, Float.NaN)

        fun parse(reader: ByteArrayReader) =
            Vector3F(reader.readFloat(), reader.readFloat(), reader.readFloat())
    }
}

fun center(vectors: Collection<Vector3F>): Vector3F {
    val (min, max) = boundingBox(vectors)
    val diff = max.minus(min)
    return diff * 0.5f + min
}

fun center(vectors: Pair<Vector3F, Vector3F>): Vector3F {
    val (min, max) = boundingBox(listOf(vectors.first, vectors.second))
    val diff = max.minus(min)
    return diff * 0.5f + min
}

fun boundingBox(vectors: Collection<Vector3F>): Pair<Vector3F, Vector3F> {
    if (vectors.isEmpty()) return Vector3F.origin to Vector3F.origin

    val min = vectors.reduce { acc, vector3F -> acc.min(vector3F) }
    val max = vectors.reduce { acc, vector3F -> acc.max(vector3F) }
    return min to max
}
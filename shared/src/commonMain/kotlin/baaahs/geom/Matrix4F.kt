package baaahs.geom

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(Matrix4FSerializer::class)
expect class Matrix4F(elements: FloatArray? = null) {
    val elements: FloatArray
    val position: Vector3F
    val translation: Vector3F
    val rotation: EulerAngle
    val scale: Vector3F

    operator fun times(matrix: Matrix4F): Matrix4F
    fun transform(vector: Vector3F): Vector3F

    fun withTranslation(translation: Vector3F): Matrix4F
    fun withRotation(rotation: EulerAngle): Matrix4F
    fun withScale(scale: Vector3F): Matrix4F

    fun inverse(): Matrix4F
    fun determinant(): Float

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String
}

val Matrix4F.Companion.identity: Matrix4F
    get() = Matrix4F()

internal expect fun matrix4F_compose(
    position: Vector3F,
    rotation: EulerAngle,
    scale: Vector3F
): Matrix4F

fun Matrix4F.Companion.compose(
    position: Vector3F = Vector3F.origin,
    rotation: EulerAngle = EulerAngle.identity,
    scale: Vector3F = Vector3F.unit3d
): Matrix4F = matrix4F_compose(position, rotation, scale)

//@Serializer(forClass = Matrix4F::class)
object Matrix4FSerializer : KSerializer<Matrix4F> {
    override val descriptor: SerialDescriptor
        get() = FloatArraySerializer().descriptor

    override fun deserialize(decoder: Decoder): Matrix4F {
        return Matrix4F(FloatArraySerializer().deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: Matrix4F) {
        FloatArraySerializer().serialize(encoder, value.elements)
    }
}

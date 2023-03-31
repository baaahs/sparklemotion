package baaahs.geom

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
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

    companion object {
        val identity: Matrix4F

        fun compose(
            position: Vector3F = Vector3F.origin,
            rotation: EulerAngle = EulerAngle.identity,
            scale: Vector3F = Vector3F.unit3d
        ): Matrix4F
    }
}

@Serializer(forClass = Matrix4F::class)
class Matrix4FSerializer : KSerializer<Matrix4F> {
    override val descriptor: SerialDescriptor
        get() = FloatArraySerializer().descriptor

    override fun deserialize(decoder: Decoder): Matrix4F {
        return Matrix4F(FloatArraySerializer().deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: Matrix4F) {
        FloatArraySerializer().serialize(encoder, value.elements)
    }
}
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

    companion object {
        val identity: Matrix4F
        fun fromPositionAndRotation(position: Vector3F, rotation: EulerAngle): Matrix4F
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
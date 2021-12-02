package baaahs.geom

import kotlinx.serialization.Serializable
import org.joml.Matrix4f as NativeMatrix4F
import org.joml.Quaternionf as NativeQuaternionF
import org.joml.Vector3f as NativeVector3F

@Serializable(Matrix4FSerializer::class)
actual class Matrix4F(private val nativeMatrix: NativeMatrix4F) {
    actual constructor(elements: FloatArray?) : this(
        NativeMatrix4F().also { if (elements != null) it.set(elements) }
    )

    actual val elements: FloatArray
        get() = nativeMatrix.get(FloatArray(16))
    actual val position: Vector3F
        get() = nativeMatrix.getTranslation(NativeVector3F()).toVector3F()
    actual val translation: Vector3F
        get() = position
    actual val rotation: EulerAngle
        get() = NativeQuaternionF().setFromUnnormalized(nativeMatrix)
            .getEulerAnglesXYZ(NativeVector3F())
            .toEulerAngle()
    actual val scale: Vector3F
        get() = nativeMatrix.getScale(NativeVector3F()).toVector3F()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix4F) return false
        return nativeMatrix == other.nativeMatrix
    }

    override fun hashCode(): Int {
        return nativeMatrix.hashCode()
    }

    actual companion object {
        actual val identity: Matrix4F
            get() = Matrix4F()

        actual fun fromPositionAndRotation(position: Vector3F, rotation: EulerAngle): Matrix4F {
            val nativeMatrix = NativeMatrix4F()
            nativeMatrix.setTranslation(position.x, position.y, position.z)
            nativeMatrix.setRotationXYZ(
                rotation.xRad.toFloat(), rotation.yRad.toFloat(), rotation.zRad.toFloat()
            )
            return Matrix4F(nativeMatrix)
        }
    }
}

private fun org.joml.Vector3f.toVector3F() =
    Vector3F(x, y, z)

private fun org.joml.Vector3f.toEulerAngle() =
    EulerAngle(this[0].toDouble(), this[1].toDouble(), this[2].toDouble())
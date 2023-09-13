package baaahs.geom

import kotlinx.serialization.Serializable
import org.joml.Matrix4f as NativeMatrix4F
import org.joml.Matrix4fc as NativeMatrix4FC
import org.joml.Quaternionf as NativeQuaternionF
import org.joml.Vector3f as NativeVector3F

@Serializable(Matrix4FSerializer::class)
actual class Matrix4F(private val nativeMatrix: NativeMatrix4FC) {
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

    actual operator fun times(matrix: Matrix4F): Matrix4F {
        val dest = NativeMatrix4F()
        nativeMatrix.mul(matrix.nativeMatrix, dest)
        return Matrix4F(dest)
    }

    actual fun transform(vector: Vector3F): Vector3F {
        return vector.toNativeVector3F().mulProject(nativeMatrix).toVector3F()
    }


    actual fun withTranslation(translation: Vector3F): Matrix4F {
        return Matrix4F(
            NativeMatrix4F(nativeMatrix).translation(translation.x, translation.y, translation.z)
        )
    }

    actual fun withRotation(rotation: EulerAngle): Matrix4F {
        return Matrix4F(
            NativeMatrix4F(nativeMatrix).rotationXYZ(
                rotation.xRad.toFloat(), rotation.yRad.toFloat(), rotation.zRad.toFloat()
            )
        )
    }

    actual fun withScale(scale: Vector3F): Matrix4F {
        return Matrix4F(
            NativeMatrix4F(nativeMatrix).scaling(translation.x, translation.y, translation.z)
        )
    }

    actual fun inverse(): Matrix4F {
        return Matrix4F(NativeMatrix4F().let { nativeMatrix.invert(it) })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix4F) return false
        return nativeMatrix == other.nativeMatrix
    }

    override fun hashCode(): Int {
        return nativeMatrix.hashCode()
    }
}

actual fun matrix4F_compose(position: Vector3F, rotation: EulerAngle, scale: Vector3F): Matrix4F =
    Matrix4F(
        NativeMatrix4F().apply {
            translationRotateScale(
                position.toNativeVector3F(),
                NativeQuaternionF().rotateXYZ(
                    rotation.xRad.toFloat(), rotation.yRad.toFloat(), rotation.zRad.toFloat()
                ),
                scale.toNativeVector3F()
            )
        }
    )

private fun Vector3F.toNativeVector3F() =
    NativeVector3F(x, y, z)

private fun org.joml.Vector3f.toVector3F() =
    Vector3F(x, y, z)

private fun org.joml.Vector3f.toEulerAngle() =
    EulerAngle(this[0].toDouble(), this[1].toDouble(), this[2].toDouble())
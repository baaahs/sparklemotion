package baaahs.geom

import baaahs.util.toDoubleArray
import kotlinx.serialization.Serializable
import three.js.Euler
import three.js.Quaternion
import three.js.Matrix4 as NativeMatrix4D
import three.js.Vector3 as NativeVector3F

@Serializable(Matrix4FSerializer::class)
actual class Matrix4F actual constructor(elements: FloatArray?) {
    private val nativeMatrix = NativeMatrix4D()
        .also { if (elements != null) it.fromArray(elements.toDoubleArray()) }

    actual val elements: FloatArray
        get() = nativeMatrix.elements.map { it.toFloat() }.toFloatArray()
    actual val position: Vector3F
        get() = NativeVector3F().setFromMatrixPosition(nativeMatrix).toVector3F()
    actual val translation: Vector3F
        get() = position
    actual val rotation: EulerAngle
        get() = Euler().setFromQuaternion(Quaternion().setFromRotationMatrix(nativeMatrix)).toEulerAngle()
    actual val scale: Vector3F
        get() = NativeVector3F().setFromMatrixScale(nativeMatrix).toVector3F()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix4F) return false
        return nativeMatrix == other.nativeMatrix
    }

    override fun hashCode(): Int {
        return nativeMatrix.elements.contentHashCode()
    }

    actual companion object {
        actual val identity: Matrix4F
            get() = Matrix4F()

        actual fun fromPositionAndRotation(position: Vector3F, rotation: EulerAngle): Matrix4F {
            val nativeMatrix = NativeMatrix4D()
            nativeMatrix.makeRotationFromEuler(rotation.toThreeEuler())
            nativeMatrix.setPosition(position.x, position.y, position.z)
            return Matrix4F(nativeMatrix.elements.map { it.toFloat() }.toFloatArray())
        }
    }
}

fun EulerAngle.toThreeEuler(): Euler =
    Euler(xRad, yRad, zRad)

private fun Euler.toEulerAngle() =
    EulerAngle(x.toDouble(), y.toDouble(), z.toDouble())
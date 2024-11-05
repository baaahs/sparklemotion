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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix4F) return false
        return nativeMatrix == other.nativeMatrix
    }

    override fun hashCode(): Int {
        return nativeMatrix.hashCode()
    }

    actual fun inverse(): Matrix4F {
        // Ensure the matrix is invertible (determinant != 0)
        val det = determinant()
        require(det != 0.0f) { "Matrix is not invertible (determinant is zero)" }

        val invElements = FloatArray(16)
        val m = elements

        // Inverse calculation logic here...
        // This code snippet assumes a row-major order for the matrix elements.
        // For a 4x4 matrix, you'll have to compute the cofactor matrix,
        // and then divide by the determinant.

        // Compute the cofactors and fill in invElements accordingly
        invElements[0] = m[5] * m[10] * m[15] - m[5] * m[11] * m[14] - m[9] * m[6] * m[15] +
                         m[9] * m[7] * m[14] + m[13] * m[6] * m[11] - m[13] * m[7] * m[10]
        invElements[1] = -m[1] * m[10] * m[15] + m[1] * m[11] * m[14] + m[9] * m[2] * m[15] -
                         m[9] * m[3] * m[14] - m[13] * m[2] * m[11] + m[13] * m[3] * m[10]
        invElements[2] = m[1] * m[6] * m[15] - m[1] * m[7] * m[14] - m[5] * m[2] * m[15] +
                         m[5] * m[3] * m[14] + m[13] * m[2] * m[7] - m[13] * m[3] * m[6]
        invElements[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2] * m[11] -
                         m[5] * m[3] * m[10] - m[9] * m[2] * m[7] + m[9] * m[3] * m[6]

        invElements[4] = -m[4] * m[10] * m[15] + m[4] * m[11] * m[14] + m[8] * m[6] * m[15] -
                         m[8] * m[7] * m[14] - m[12] * m[6] * m[11] + m[12] * m[7] * m[10]
        invElements[5] = m[0] * m[10] * m[15] - m[0] * m[11] * m[14] - m[8] * m[2] * m[15] +
                         m[8] * m[3] * m[14] + m[12] * m[2] * m[11] - m[12] * m[3] * m[10]
        invElements[6] = -m[0] * m[6] * m[15] + m[0] * m[7] * m[14] + m[4] * m[2] * m[15] -
                         m[4] * m[3] * m[14] - m[12] * m[2] * m[7] + m[12] * m[3] * m[6]
        invElements[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2] * m[11] +
                         m[4] * m[3] * m[10] + m[8] * m[2] * m[7] - m[8] * m[3] * m[6]

        invElements[8] = m[4] * m[9] * m[15] - m[4] * m[11] * m[13] - m[8] * m[5] * m[15] +
                         m[8] * m[7] * m[13] + m[12] * m[5] * m[11] - m[12] * m[7] * m[9]
        invElements[9] = -m[0] * m[9] * m[15] + m[0] * m[11] * m[13] + m[8] * m[1] * m[15] -
                         m[8] * m[3] * m[13] - m[12] * m[1] * m[11] + m[12] * m[3] * m[9]
        invElements[10] = m[0] * m[5] * m[15] - m[0] * m[7] * m[13] - m[4] * m[1] * m[15] +
                          m[4] * m[3] * m[13] + m[12] * m[1] * m[7] - m[12] * m[3] * m[5]
        invElements[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1] * m[11] -
                          m[4] * m[3] * m[9] - m[8] * m[1] * m[7] + m[8] * m[3] * m[5]

        invElements[12] = -m[4] * m[9] * m[14] + m[4] * m[10] * m[13] + m[8] * m[5] * m[14] -
                          m[8] * m[6] * m[13] - m[12] * m[5] * m[10] + m[12] * m[6] * m[9]
        invElements[13] = m[0] * m[9] * m[14] - m[0] * m[10] * m[13] - m[8] * m[1] * m[14] +
                          m[8] * m[2] * m[13] + m[12] * m[1] * m[10] - m[12] * m[2] * m[9]
        invElements[14] = -m[0] * m[5] * m[14] + m[0] * m[6] * m[13] + m[4] * m[1] * m[14] -
                          m[4] * m[2] * m[13] - m[12] * m[1] * m[6] + m[12] * m[2] * m[5]
        invElements[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1] * m[10] +
                          m[4] * m[2] * m[9] + m[8] * m[1] * m[6] - m[8] * m[2] * m[5]


        // Continue filling in invElements with the correct cofactors...

        // Finally, divide each element by the determinant
        for (i in invElements.indices) {
            invElements[i] /= det
        }

        return Matrix4F(invElements)
    }

    actual fun determinant(): Float {
        // Calculate the determinant of the 4x4 matrix
        // For a 4x4 matrix in elements[], the determinant calculation will be extensive.
        // Example:
        return elements[0] * (
            elements[5] * (elements[10] * elements[15] - elements[11] * elements[14]) -
            elements[9] * (elements[6] * elements[15] - elements[7] * elements[14]) +
            elements[13] * (elements[6] * elements[11] - elements[7] * elements[10])
        ) -
        elements[4] * (
            elements[1] * (elements[10] * elements[15] - elements[11] * elements[14]) -
            elements[9] * (elements[2] * elements[15] - elements[3] * elements[14]) +
            elements[13] * (elements[2] * elements[11] - elements[3] * elements[10])
        ) +
        elements[8] * (
            elements[1] * (elements[6] * elements[15] - elements[7] * elements[14]) -
            elements[5] * (elements[2] * elements[15] - elements[3] * elements[14]) +
            elements[13] * (elements[2] * elements[7] - elements[3] * elements[6])
        ) -
        elements[12] * (
            elements[1] * (elements[6] * elements[11] - elements[7] * elements[10]) -
            elements[5] * (elements[2] * elements[11] - elements[3] * elements[10]) +
            elements[9] * (elements[2] * elements[7] - elements[3] * elements[6])
        )
    }
}

actual fun matrix4F_compose(position: Vector3F, rotation: EulerAngle, scale: Vector3F): Matrix4F =
    Matrix4F(
        NativeMatrix4F().apply {
            translationRotateScale(
                position.toNativeVector3F(),
                //NativeQuaternionF().rotateXYZ(
                //    rotation.xRad.toFloat(), rotation.yRad.toFloat(), rotation.zRad.toFloat()
                //),
                NativeQuaternionF().rotateZYX(
                    rotation.zRad.toFloat(), rotation.yRad.toFloat(), rotation.xRad.toFloat()
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

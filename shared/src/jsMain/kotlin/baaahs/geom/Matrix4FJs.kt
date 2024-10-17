package baaahs.geom

import baaahs.util.toDoubleArray
import baaahs.visualizer.toVector3
import kotlinx.serialization.Serializable
import three.Euler
import three.Object3D
import three.Quaternion
import three_ext.set
import three_ext.toVector3F
import three.Matrix4 as NativeMatrix4D
import three.Vector3 as NativeVector3F

@Serializable(Matrix4FSerializer::class)
actual class Matrix4F actual constructor(elements: FloatArray?) {
    constructor(nativeMatrix: three.Matrix4) : this(nativeMatrix.elements.map { it.toFloat() }.toFloatArray())

    val nativeMatrix = NativeMatrix4D()
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

    actual operator fun times(matrix: Matrix4F): Matrix4F {
        return Matrix4F(nativeMatrix.clone().multiply(matrix.nativeMatrix))
    }

    actual fun transform(vector: Vector3F): Vector3F {
        return vector.toVector3().applyMatrix4(nativeMatrix).toVector3F()
    }

    actual fun withTranslation(translation: Vector3F): Matrix4F {
        return alter { pTranslation, _, _ ->
            pTranslation.set(translation)
        }
    }

    actual fun withRotation(rotation: EulerAngle): Matrix4F {
        return alter { _, pRotation, _ ->
            pRotation.setFromEuler(rotation.toThreeEuler())
        }
    }

    actual fun withScale(scale: Vector3F): Matrix4F {
        return alter { _, _, pScale ->
            pScale.set(scale)
        }
    }

    private fun alter(
        block: (translation: three.Vector3, rotation: Quaternion, scale: three.Vector3) -> Unit
    ): Matrix4F {
        val translation = three.Vector3()
        val rotation = Quaternion()
        val scale = three.Vector3()
        nativeMatrix.decompose(translation, rotation, scale)
        block(translation, rotation, scale)
        return Matrix4F(nativeMatrix.compose(translation, rotation, scale))
    }

    fun copyTo(object3D: Object3D) {
        nativeMatrix.decompose(object3D.position, object3D.quaternion, object3D.scale)
        object3D.matrix.copy(nativeMatrix)
        object3D.matrixWorldNeedsUpdate = true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix4F) return false
        return nativeMatrix == other.nativeMatrix
    }

    override fun hashCode(): Int {
        return nativeMatrix.elements.contentHashCode()
    }

    actual fun inverse(): Matrix4F {
        // Ensure the matrix is invertible (determinant != 0)
        val det = determinant()
        require(det != 0.0f) { "Matrix is not invertible (determinant is zero)" }

        val invElements = FloatArray(16)
	val m = elements;

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
        NativeMatrix4D().compose(
            position.toVector3(),
            Quaternion().apply { setFromEuler(rotation.toThreeEuler()) },
            scale.toVector3()
        )
    )

fun EulerAngle.toThreeEuler(): Euler =
    Euler(xRad, yRad, zRad, "ZYX")

fun Euler.toEulerAngle() =
    EulerAngle(x.toDouble(), y.toDouble(), z.toDouble())

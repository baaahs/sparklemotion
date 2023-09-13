package baaahs.geom

import baaahs.util.toDoubleArray
import baaahs.visualizer.toVector3
import kotlinx.serialization.Serializable
import three.js.Euler
import three.js.Object3D
import three.js.Quaternion
import three_ext.set
import three_ext.toVector3F
import three.js.Matrix4 as NativeMatrix4F
import three.js.Vector3 as NativeVector3F

@Serializable(Matrix4FSerializer::class)
actual class Matrix4F actual constructor(elements: FloatArray?) {
    constructor(nativeMatrix: three.js.Matrix4) : this(nativeMatrix.elements.map { it.toFloat() }.toFloatArray())

    val nativeMatrix = NativeMatrix4F()
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

    actual fun inverse(): Matrix4F {
        return Matrix4F(NativeMatrix4F().let { nativeMatrix.getInverse(it) })
    }

    private fun alter(
        block: (translation: three.js.Vector3, rotation: Quaternion, scale: three.js.Vector3) -> Unit
    ): Matrix4F {
        val translation = three.js.Vector3()
        val rotation = Quaternion()
        val scale = three.js.Vector3()
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
}

actual fun matrix4F_compose(position: Vector3F, rotation: EulerAngle, scale: Vector3F): Matrix4F =
    Matrix4F(
        NativeMatrix4F().compose(
            position.toVector3(),
            Quaternion().apply { setFromEuler(rotation.toThreeEuler()) },
            scale.toVector3()
        )
    )

fun EulerAngle.toThreeEuler(): Euler =
    Euler(xRad, yRad, zRad)

fun Euler.toEulerAngle() =
    EulerAngle(x.toDouble(), y.toDouble(), z.toDouble())
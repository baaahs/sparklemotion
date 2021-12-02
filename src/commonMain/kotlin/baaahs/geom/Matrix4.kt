package baaahs.geom

import kotlinx.serialization.Serializable

@Serializable
class Matrix4(val elements: DoubleArray = DoubleArray(16) { 0.0 }) {
    val position get() = getPositionFromMatrix(this)
    val translation get() = position
    val rotation get() = getRotationFromMatrix(this)

    companion object {
        val identity = Matrix4()

        fun fromPositionAndRotation(position: Vector3F, rotation: EulerAngle) =
            createMatrixWithPositionAndRotation(position, rotation)
    }
}

internal expect fun createMatrixWithPositionAndRotation(position: Vector3F, rotation: EulerAngle): Matrix4
internal expect fun getPositionFromMatrix(matrix: Matrix4): Vector3F
internal expect fun getRotationFromMatrix(matrix: Matrix4): EulerAngle
package baaahs.geom

import kotlinx.serialization.Serializable

@Serializable
class Matrix4(val elements: DoubleArray = DoubleArray(16) { 0.0 }) {
    companion object {
        fun fromPositionAndOrientation(position: Vector3F, rotation: EulerAngle) =
            createMatrixWithPositionAndRotation(position, rotation)
    }
}

expect fun createMatrixWithPositionAndRotation(position: Vector3F, rotation: EulerAngle): Matrix4
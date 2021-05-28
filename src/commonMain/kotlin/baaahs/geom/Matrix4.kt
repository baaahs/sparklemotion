package baaahs.geom

import kotlinx.serialization.Serializable

@Serializable
class Matrix4(val elements: DoubleArray = DoubleArray(16) { 0.0 }) {
    companion object {
        fun fromPositionAndOrientation(origin: Vector3F, heading: Vector3F) =
            createMatrixWithPositionAndOrientation(origin, heading)
    }
}

expect fun createMatrixWithPositionAndOrientation(origin: Vector3F, heading: Vector3F): Matrix4
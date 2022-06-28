package baaahs.mapper

import baaahs.geom.Vector3F
import kotlinx.serialization.Serializable

@Serializable
data class MapperData(
    val cameraPositions: Map<String, CameraPosition>
)

@Serializable
data class CameraPosition(
    val position: Vector3F,
    val target: Vector3F,
    val zRotation: Double
)
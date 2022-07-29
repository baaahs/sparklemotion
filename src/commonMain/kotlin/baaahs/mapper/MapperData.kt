package baaahs.mapper

import kotlinx.serialization.Serializable

@Serializable
data class MapperData(
    val cameraPositions: Map<String, CameraPosition>
)
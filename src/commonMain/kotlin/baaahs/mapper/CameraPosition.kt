package baaahs.mapper

import baaahs.geom.Vector3F
import kotlinx.serialization.Serializable

@Serializable
data class CameraPosition(
    val position: Vector3F,
    val target: Vector3F,
    val zoom: Double = 1.0,
    val focalOffset: Vector3F = Vector3F.origin,
    val zRotation: Double = 0.0
)
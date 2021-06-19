package baaahs.mapper

import baaahs.BrainManager
import baaahs.geom.Matrix4
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import com.soywiz.klock.DateTime
import kotlinx.serialization.Serializable

@Serializable
data class MappingSession(
    val startedAt: Double,
    val surfaces: List<SurfaceData>,
    val cameraMatrix: Matrix4? = null,
    val baseImage: String? = null,
    val version: Int = 0,
    val savedAt: Double = DateTime.nowUnix(),
    val notes: String? = null
) {
    val startedAtDateTime: DateTime get() = DateTime(startedAt)

    @Serializable
    data class SurfaceData(
        val controllerType: String? = BrainManager.controllerTypeName,
        val brainId: String, // TODO: rename to controllerId.
        val panelName: String, // TODO: rename to entityName.
        val pixels: List<PixelData?>,
        val deltaImage: String? = null,
        val screenAreaInSqPixels: Float? = null,
        val screenAngle: Float? = null
    ) {
        val controllerId: ControllerId get() =
            ControllerId(controllerType ?: BrainManager.controllerTypeName, brainId)
        val entityName: String get() = panelName

        @Serializable
        data class PixelData(
            val modelPosition: Vector3F?,
            val screenPosition: Vector2F?,
            val deltaImage: String?
        )
    }
}
package baaahs.mapper

import baaahs.geom.Matrix4
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import com.soywiz.klock.DateTime
import kotlinx.serialization.Serializable

@Serializable
data class MappingSession(
    val startedAt: Double,
    val surfaces: List<SurfaceData>,
    val cameraMatrix: Matrix4,
    val baseImage: String?,
    val version: Int = 0,
    val savedAt: Double = DateTime.nowUnix(),
    val notes: String? = null
) {
    val startedAtDateTime: DateTime get() = DateTime(startedAt)

    @Serializable
    data class SurfaceData(
        val brainId: String,
        val panelName: String,
        val pixels: List<PixelData?>,
        val deltaImage: String?,
        val screenAreaInSqPixels: Float?,
        val screenAngle: Float?
    ) {

        @Serializable
        data class PixelData(
            val modelPosition: Vector3F?,
            val screenPosition: Vector2F?,
            val deltaImage: String?
        )
    }
}
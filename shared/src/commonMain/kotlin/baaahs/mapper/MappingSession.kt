package baaahs.mapper

import baaahs.controller.ControllerId
import baaahs.device.PixelFormat
import baaahs.dmx.DmxTransportConfig
import baaahs.geom.Matrix4F
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.sm.brain.BrainManager
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MappingSession(
    val startedAt: Instant,
    val surfaces: List<SurfaceData>, // TODO: Rename to entities.
    val cameraMatrix: Matrix4F? = null, // TODO: Remove.
    val cameraPosition: CameraPosition? = null,
    val baseImage: String? = null,
    val metadata: MappingStrategy.SessionMetadata? = null,
    val version: Int = 0,
    val savedAt: Instant?,
    val notes: String? = null
) {
    @Serializable
    data class SurfaceData(
        val controllerType: String? = BrainManager.controllerTypeName,
        val brainId: String, // TODO: rename to controllerId.
        val panelName: String, // TODO: rename to entityName.
        val pixelCount: Int? = null,
        val pixelFormat: PixelFormat? = PixelFormat.RGB8,
        val pixels: List<PixelData?>? = null,
        val deltaImage: String? = null,
        val screenAreaInSqPixels: Float? = null,
        val screenAngle: Float? = null,
        val channels: DmxTransportConfig? = null
    ) {
        val controllerId: ControllerId
            get() =
            ControllerId(controllerType ?: BrainManager.controllerTypeName, brainId)
        val entityName: String get() = panelName

        @Serializable
        data class PixelData(
            val modelPosition: Vector3F?,
            val screenPosition: Vector2F? = null,
            val deltaImage: String? = null, // TODO: Remove this.
            val metadata: MappingStrategy.PixelMetadata? = null
        )

        /**
         * A range of DMX channels.
         *
         * For example, a device using 16 channels starting from the first channel in a universe would
         * be `Channels(0, 15)`, and represent DMX channels 1 through 16. Ick.
         *
         * @param start Starting channel, _zero based_.
         * @param end Ending channel, inclusive, _zero based_.
         */
        @Serializable
        data class Channels(
            val start: Int,
            val end: Int
        )
    }
}
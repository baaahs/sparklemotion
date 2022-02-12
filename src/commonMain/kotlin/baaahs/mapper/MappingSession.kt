package baaahs.mapper

import baaahs.controller.ControllerId
import baaahs.fixtures.FixtureConfig
import baaahs.geom.Matrix4F
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.sm.brain.BrainManager
import com.soywiz.klock.DateTime
import kotlinx.serialization.Serializable

@Serializable
data class MappingSession(
    val startedAt: Double,
    val surfaces: List<SurfaceData>, // TODO: rename to items?
    val cameraMatrix: Matrix4F? = null,
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
        val pixelCount: Int? = null,
        val pixels: List<PixelData?>? = null,
        val fixtureConfig: FixtureConfig? = null,
        val deltaImage: String? = null,
        val screenAreaInSqPixels: Float? = null,
        val screenAngle: Float? = null,
        val channels: Channels? = null
    ) {
        val controllerId: ControllerId
            get() =
            ControllerId(controllerType ?: BrainManager.controllerTypeName, brainId)
        val entityName: String get() = panelName

        @Serializable
        data class PixelData(
            val modelPosition: Vector3F?,
            val screenPosition: Vector2F? = null,
            val deltaImage: String? = null
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
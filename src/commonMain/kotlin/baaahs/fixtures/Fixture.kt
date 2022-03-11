package baaahs.fixtures

import baaahs.device.FixtureType
import baaahs.device.MovingHeadDevice
import baaahs.device.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.model.MovingHeadAdapter
import baaahs.sim.FixtureSimulation
import kotlinx.serialization.Serializable

/**
 * Represents a controllable lighting fixture.
 *
 * May or may not be associated with a [Model.Entity]. Note that there may be more than
 * one fixture for any given entity.
 *
 * TODO: Fixture shouldn't contain references to pixels, those only make sense for
 * TODO: [PixelArrayDevice] fixtures, so pixel data should live in their [FixtureConfig].
 */
abstract class Fixture(
    val modelEntity: Model.Entity?,
    val pixelCount: Int,
    val name: String = modelEntity?.name ?: "Anonymous fixture",
    val transport: Transport
) {
    open val componentCount: Int
        get() = pixelCount

    abstract val fixtureType: FixtureType
    abstract val remoteConfig: RemoteConfig

    val title: String
        get() = "$name: ${fixtureType.title} with $pixelCount pixels at ${transport.name}"

    override fun toString() = "Fixture[${hashCode()} $title]"
}

interface RemoteConfig {
    fun receiveRemoteVisualizationFixtureInfo(reader: ByteArrayReader, fixtureSimulation: FixtureSimulation) = Unit
}

interface FixturePreview {
    val fixtureConfig: ConfigPreview
    val transportConfig: ConfigPreview
}

interface ConfigPreview {
    fun summary(): List<Pair<String, String?>>
}

@Serializable
data class PixelArrayRemoteConfig(
    val entityId: String?,
    val pixelCount: Int,
    val name: String,
    val pixelFormat: PixelArrayDevice.PixelFormat,
    val gammaCorrection: Float,
    val pixelLocations: List<Vector3F>
) : RemoteConfig {
    override fun receiveRemoteVisualizationFixtureInfo(reader: ByteArrayReader, fixtureSimulation: FixtureSimulation) {
        val pixelCount = reader.readInt()
        val pixelLocations = (0 until pixelCount).map {
            Vector3F.parse(reader)
        }.toTypedArray()

        fixtureSimulation.updateVisualizerWith(this, pixelCount, pixelLocations)
    }
}

class PixelArrayFixture(
    modelEntity: Model.Entity?,
    pixelCount: Int,
    name: String = modelEntity?.name ?: "Anonymous fixture",
    transport: Transport,
    val pixelFormat: PixelArrayDevice.PixelFormat = PixelArrayDevice.PixelFormat.default,
    val gammaCorrection: Float = 1f,
    /** Each pixel's location relative to the fixture. */
    val pixelLocations: List<Vector3F> = emptyList()
) : Fixture(modelEntity, pixelCount, name, transport) {
    override val fixtureType: FixtureType
        get() = PixelArrayDevice
    override val remoteConfig: RemoteConfig
        get() = PixelArrayRemoteConfig(
            modelEntity?.name, pixelCount, name, pixelFormat, gammaCorrection, pixelLocations
        )
}

@Serializable
data class MovingHeadRemoteConfig(
    val entityId: String?,
    val pixelCount: Int,
    val name: String,
    val adapter: MovingHeadAdapter
) : RemoteConfig

class MovingHeadFixture(
    modelEntity: Model.Entity?,
    pixelCount: Int,
    name: String = modelEntity?.name ?: "Anonymous fixture",
    transport: Transport,
    val adapter: MovingHeadAdapter
) : Fixture(modelEntity, pixelCount, name, transport) {
    override val fixtureType: FixtureType
        get() = MovingHeadDevice
    override val remoteConfig: RemoteConfig
        get() = MovingHeadRemoteConfig(
            modelEntity?.name, pixelCount, name, adapter
        )
}
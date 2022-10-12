package baaahs.fixtures

import baaahs.device.*
import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.model.MovingHeadAdapter

/**
 * Represents a controllable lighting fixture.
 *
 * May or may not be associated with a [Model.Entity]. Note that there may be more than
 * one fixture for a given entity.
 */
class Fixture(
    val modelEntity: Model.Entity?,
    val componentCount: Int,
    val name: String = modelEntity?.name ?: "Anonymous fixture",
    val transport: Transport,
    val fixtureType: FixtureType,
    val fixtureConfig: FixtureConfig
) {
    val title: String
        get() = "$name: ${fixtureType.title} with $componentCount components at ${transport.name}"

    override fun toString() = "Fixture[${hashCode()} $title]"
}

interface RemoteConfig {
//    val fixtureType: FixtureType
}

interface FixturePreview {
    val fixtureOptions: ConfigPreview
    val transportConfig: ConfigPreview
}

class FixturePreviewError(val e: Exception) : FixturePreview {
    override val fixtureOptions: ConfigPreview
        get() = TODO("not implemented")
    override val transportConfig: ConfigPreview
        get() = TODO("not implemented")

}

interface ConfigPreview {
    fun summary(): List<Pair<String, String?>>
}

class PixelArrayFixture {
    companion object {
        fun from(
            modelEntity: Model.Entity?,
            pixelCount: Int,
            name: String = modelEntity?.name ?: "Anonymous fixture",
            transport: Transport,
            pixelFormat: PixelFormat = PixelFormat.default,
            gammaCorrection: Float = 1f,
            /** Each pixel's location relative to the fixture. */
            pixelLocations: List<Vector3F> = emptyList()
        ): Fixture =
            Fixture(
                modelEntity, pixelCount, name, transport,
                PixelArrayDevice,
                PixelArrayDevice.Config(
                    pixelCount, pixelFormat, gammaCorrection, EnumeratedPixelLocations(pixelLocations)
                )
            )
    }
}

class MovingHeadFixture {
    companion object {
        fun from(
            modelEntity: Model.Entity?,
            componentCount: Int,
            name: String = modelEntity?.name ?: "Anonymous fixture",
            transport: Transport,
            adapter: MovingHeadAdapter
        ) =
            Fixture(modelEntity, componentCount, name, transport, MovingHeadDevice,
                MovingHeadDevice.Config(adapter)
            )
    }
}
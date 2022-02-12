package baaahs.fixtures

import baaahs.device.DeviceType
import baaahs.geom.Vector3F
import baaahs.model.Model

/**
 * Represents a controllable lighting fixture.
 *
 * May or may not be associated with a [Model.Entity]. Note that there may be more than
 * one fixture for any given entity.
 *
 * TODO: Fixture shouldn't contain references to pixels, those only make sense for
 * TODO: [PixelArrayDevice] fixtures, so pixel data should live in their [FixtureConfig].
 */
class Fixture(
    val modelEntity: Model.Entity?,
    val pixelCount: Int,
    /** Each pixel's location in the global 3d model. */
    val pixelLocations: List<Vector3F>,
    val fixtureConfig: FixtureConfig,
    val name: String = modelEntity?.name ?: "Anonymous fixture",
    val transport: Transport
) {
    val deviceType: DeviceType
        get() = fixtureConfig.deviceType

    val title: String
        get() = "$name: ${deviceType.title} with $pixelCount pixels at ${transport.name}"

    override fun toString() = "Fixture[${hashCode()} $title]"
}

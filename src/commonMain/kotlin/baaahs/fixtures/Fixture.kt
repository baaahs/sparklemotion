package baaahs.fixtures

import baaahs.geom.Vector3F
import baaahs.model.Model

/**
 * Represents a controllable lighting fixture.
 *
 * May or may not be associated with a [Model.Entity].
 */
class Fixture(
    val modelEntity: Model.Entity?,
    val pixelCount: Int,
    /** Each pixel's location in the global 3d model. */
    val pixelLocations: List<Vector3F>,
    val deviceType: DeviceType,
    val name: String = modelEntity?.name ?: "Anonymous fixture",
    val transport: Transport
) {
    val title: String get() =
        "$name: ${deviceType.title} with $pixelCount pixels at ${transport.name}"

    override fun toString() = "Fixture[${hashCode()} $title]"
}

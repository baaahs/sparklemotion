package baaahs.fixtures

import baaahs.geom.Vector3F
import baaahs.model.Model

/**
 * Represents a controllable lighting fixture.
 *
 * May or may not be associated with a [Model.Surface].
 */
class Fixture(
    val modelSurface: Model.Surface?,
    val pixelCount: Int,
    /** Each pixel's location in the global 3d model. */
    val pixelLocations: List<Vector3F>,
    val deviceType: DeviceType,
    val name: String = modelSurface?.name ?: "Anonymous fixture"
) {
    fun describe(): String = modelSurface?.description ?: name
}

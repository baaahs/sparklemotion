package baaahs.fixtures

import baaahs.geom.Vector3F
import baaahs.model.Model

class FixtureMapping(
    val entity: Model.Entity?,

    @Deprecated("Use fixtureConfig for pixelCount instead.")
    val pixelCount: Int? = null,

    /** Pixel's estimated position within the model. */
    @Deprecated("Use fixtureConfig for pixelLocations instead.")
    val pixelLocations: List<Vector3F?>? = null,

    val fixtureConfig: FixtureConfig? = null,

    val transportConfig: TransportConfig? = null
)
package baaahs.mapper

import baaahs.fixtures.FixtureConfig
import baaahs.geom.Vector3F
import baaahs.model.Model

class FixtureMapping(
    val entity: Model.Entity?,

    val pixelCount: Int? = null,

    /** Pixel's estimated position within the model. */
    val pixelLocations: List<Vector3F?>? = null,

    val fixtureConfig: FixtureConfig? = null,

    val transportConfig: TransportConfig? = null
)

interface TransportConfig

data class SacnTransportConfig(
    val startChannel: Int,
    val endChannel: Int,
    val componentsStartAtUniverseBoundaries: Boolean = true
) : TransportConfig
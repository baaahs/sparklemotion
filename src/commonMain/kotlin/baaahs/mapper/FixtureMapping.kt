package baaahs.mapper

import baaahs.geom.Vector3F
import baaahs.model.Model

class FixtureMapping(
    val entity: Model.Entity,

    /** Pixel's estimated position within the model. */
    val pixelLocations: List<Vector3F?>?
)
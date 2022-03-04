package baaahs.fixtures

import baaahs.device.FixtureType
import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.scene.MutableFixtureConfig

interface FixtureConfig {
    val componentCount: Int?

    val fixtureType: FixtureType

    fun edit(): MutableFixtureConfig

    fun generatePixelLocations(pixelCount: Int, entity: Model.Entity?, model: Model): List<Vector3F>? = null

    /** Merges two configs, preferring values from [other]. */
    operator fun plus(other: FixtureConfig?): FixtureConfig
}
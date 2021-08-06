package baaahs.fixtures

import baaahs.device.DeviceType
import baaahs.geom.Vector3F
import baaahs.model.Model

interface FixtureConfig {
    val deviceType: DeviceType

    fun generatePixelLocations(pixelCount: Int, entity: Model.Entity?, model: Model): List<Vector3F>? = null
}
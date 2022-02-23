package baaahs.fixtures

import baaahs.device.DeviceType
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.scene.MutableFixtureConfig
import baaahs.sim.FixtureSimulation

interface FixtureConfig {
    val componentCount: Int?

    val deviceType: DeviceType

    fun edit(): MutableFixtureConfig

    fun generatePixelLocations(pixelCount: Int, entity: Model.Entity?, model: Model): List<Vector3F>? = null

    fun receiveRemoteVisualizationFixtureInfo(reader: ByteArrayReader, fixtureSimulation: FixtureSimulation) = Unit
}
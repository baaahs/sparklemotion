package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureConfig
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.visualizer.ItemVisualizer

interface FixtureSimulation {
    val mappingData: MappingSession.SurfaceData?
    val itemVisualizer: ItemVisualizer<*>
    val previewFixture: Fixture?

    fun start() {}
    fun stop() {}

    fun updateVisualizerWith(fixtureConfig: FixtureConfig, pixelCount: Int, pixelLocations: Array<Vector3F>) {
        // No-op by default.
    }

    fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        // No-op by default.
    }
}


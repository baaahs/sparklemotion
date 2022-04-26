package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.fixtures.RemoteConfig
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.visualizer.ItemVisualizer

interface FixtureSimulation {
    val mappingData: MappingSession.SurfaceData?
    val itemVisualizer: ItemVisualizer<*>
    val previewFixture: Fixture?

    fun start() {}
    fun stop() {}

    fun updateVisualizerWith(remoteConfig: RemoteConfig) {
        // No-op by default.
    }

    fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        // No-op by default.
    }
}


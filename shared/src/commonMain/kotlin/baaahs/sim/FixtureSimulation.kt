package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.mapper.MappingSession
import baaahs.visualizer.entity.ItemVisualizer

interface FixtureSimulation {
    val mappingData: MappingSession.SurfaceData?
    val itemVisualizer: ItemVisualizer<*>
    val previewFixture: Fixture?

    fun start() {}
    fun stop() {}
}


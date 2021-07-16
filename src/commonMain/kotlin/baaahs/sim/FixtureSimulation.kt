package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.io.ByteArrayReader
import baaahs.mapper.MappingSession
import baaahs.model.Model
import baaahs.visualizer.EntityVisualizer

interface FixtureSimulation {
    val mappingData: MappingSession.SurfaceData?
    val entityVisualizer: EntityVisualizer
    val previewFixture: Fixture

    fun launch()

    fun receiveRemoteVisualizationFixtureInfo(reader: ByteArrayReader) {
        // No-op by default.
    }

    fun receiveRemoteVisualizationFrameData(reader: ByteArrayReader) {
        // No-op by default.
    }
}

fun interface FixtureSimulationBuilder {
    fun build(entity: Model.Entity, simulationEnv: SimulationEnv): FixtureSimulation
}

package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.mapper.MappingSession
import baaahs.visualizer.EntityVisualizer

interface FixtureSimulation {
    val mappingData: MappingSession.SurfaceData?
    val entityVisualizer: EntityVisualizer
    val previewFixture: Fixture

    fun launch()
}


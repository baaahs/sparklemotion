package baaahs.sim

import baaahs.fixtures.Fixture
import baaahs.mapper.MappingSession
import baaahs.visualizer.FakeItemVisualizer

class FakeFixtureSimulation(
    override val itemVisualizer: FakeItemVisualizer,
    override val mappingData: MappingSession.SurfaceData? = null,
    override val previewFixture: Fixture? = null
) : FixtureSimulation
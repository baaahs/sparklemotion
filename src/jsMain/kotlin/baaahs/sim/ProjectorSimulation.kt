package baaahs.sim

import baaahs.device.ProjectorDevice
import baaahs.fixtures.Fixture
import baaahs.fixtures.NullTransport
import baaahs.mapper.MappingSession
import baaahs.model.Projector
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.ProjectorVisualizer

actual class ProjectorSimulation actual constructor(
    projector: Projector,
    adapter: EntityAdapter
) : FixtureSimulation {
    override val mappingData: MappingSession.SurfaceData?
        get() = null

    override val itemVisualizer by lazy { ProjectorVisualizer(projector, adapter) }

    override val previewFixture: Fixture by lazy {
        Fixture(projector, 1, projector.name, NullTransport, ProjectorDevice, ProjectorDevice.Config()
    }
}
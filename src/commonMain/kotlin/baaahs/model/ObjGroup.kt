package baaahs.model

import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.mapper.MappingSession
import baaahs.sim.FixtureSimulation
import baaahs.sim.SimulationEnv
import baaahs.visualizer.EntityVisualizer
import baaahs.visualizer.visualizerBuilder

class ObjGroup(
    override val name: String,
    override val description: String?,
    override val transformation: Matrix4F,
    metadata: EntityMetadataProvider?,
    loader: ObjModelLoader
) : Model.EntityGroup {
    override val deviceType: DeviceType get() = PixelArrayDevice // TODO
    override val bounds: Pair<Vector3F, Vector3F> = boundingBox(loader.geomVertices)
    override val entities: List<Model.Entity> = loader.allEntities.map { it.transform(transformation) }

    override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
        object : FixtureSimulation {
            override val mappingData: MappingSession.SurfaceData? get() = null
            override val entityVisualizer: EntityVisualizer = createVisualizer(simulationEnv)
            override val previewFixture: Fixture? get() = null

            override fun launch() {
                // No-op?
            }
        }

    override fun createVisualizer(simulationEnv: SimulationEnv): EntityVisualizer =
        visualizerBuilder.createObjGroupVisualizer(this, simulationEnv)
}
package baaahs.model

import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.mapper.MappingSession
import baaahs.sim.FixtureSimulation
import baaahs.sim.SimulationEnv
import baaahs.visualizer.EntityVisualizer
import baaahs.visualizer.visualizerBuilder

class ObjGroup(
    override val name: String,
    override val description: String? = null,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    metadata: EntityMetadataProvider?,
    loader: ObjModelLoader
) : Model.BaseEntity(), Model.EntityGroup {
    override val deviceType: DeviceType get() = PixelArrayDevice // TODO
    override val bounds: Pair<Vector3F, Vector3F> = boundingBox(loader.geomVertices)
    override val entities: List<Model.Entity> = loader.allEntities // .map { it.transform(transformation) }

    override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
        ObjGroupSimulation(simulationEnv)

    inner class ObjGroupSimulation(private val simulationEnv: SimulationEnv) : FixtureSimulation {
        override val mappingData: MappingSession.SurfaceData? get() = null
        override val entityVisualizer: EntityVisualizer<*> = createVisualizer(simulationEnv)
        override val previewFixture: Fixture? get() = null

        override fun launch() {
            // No-op.
        }
    }

    override fun createVisualizer(simulationEnv: SimulationEnv): EntityVisualizer<*> =
        visualizerBuilder.createObjGroupVisualizer(this, simulationEnv)
}
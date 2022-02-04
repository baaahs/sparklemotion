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
import baaahs.sm.webapi.Problem
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.ItemVisualizer
import kotlinx.serialization.Transient

class ObjGroup(
    override val name: String,
    override val description: String? = null,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    metadata: EntityMetadataProvider?,
    objData: String,
    objDataIsFileRef: Boolean,
    @Transient
    override val id: EntityId = Model.Entity.nextId()
) : Model.BaseEntity(), Model.EntityGroup {
    override val deviceType: DeviceType get() = PixelArrayDevice // TODO

    private val loader: ObjModelLoader?
    private val loaderError: Exception?
    init {
        val x = try {
            ObjModelLoader.doImport(objData, objDataIsFileRef, title) {
                metadata?.getMetadataFor(this)?.expectedPixelCount
            } to null
        } catch (e: Exception) {
            null to e
        }
        loader = x.first
        loaderError = x.second
    }

    override val bounds: Pair<Vector3F, Vector3F> = boundingBox(loader?.geomVertices ?: emptyList())
    override val entities: List<Model.Entity> = loader?.allEntities ?: emptyList()
    // .map { it.transform(transformation) }

    override val problems: Collection<Problem> =
        (loader?.errors ?: emptyList())
            .map { Problem("Import error: $name", it.message) } +
        listOfNotNull(loaderError)
            .map { Problem("Import error: $name", loaderError?.message ?: "Unknown error.") }

    override fun createFixtureSimulation(simulationEnv: SimulationEnv, adapter: EntityAdapter): FixtureSimulation =
        ObjGroupSimulation(adapter)

    inner class ObjGroupSimulation(adapter: EntityAdapter) : FixtureSimulation {
        override val mappingData: MappingSession.SurfaceData? get() = null
        override val itemVisualizer: ItemVisualizer<*> = createVisualizer(adapter)
        override val previewFixture: Fixture? get() = null

        override fun start() {
            // No-op.
        }
    }

    override fun createVisualizer(adapter: EntityAdapter) =
        adapter.createObjGroupVisualizer(this)
}
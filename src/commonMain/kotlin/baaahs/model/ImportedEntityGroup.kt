package baaahs.model

import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.mapper.MappingSession
import baaahs.model.importers.ObjImporter
import baaahs.sim.FixtureSimulation
import baaahs.sim.SimulationEnv
import baaahs.sm.webapi.Problem
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.ItemVisualizer
import kotlinx.serialization.Transient

class ImportedEntityGroup(
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

    private val importerResults: Importer.Results?
    private val importerError: Exception?
    init {
        val results = try {
            ObjImporter.doImport(objData, objDataIsFileRef, title) {
                metadata?.getMetadataFor(this)?.expectedPixelCount
            } to null
        } catch (e: Exception) {
            null to e
        }
        importerResults = results.first
        importerError = results.second
    }

    override val bounds: Pair<Vector3F, Vector3F> = boundingBox(importerResults?.vertices ?: emptyList())
    override val entities: List<Model.Entity> = importerResults?.entities ?: emptyList()
    // .map { it.transform(transformation) }

    override val problems: Collection<Problem> =
        (importerResults?.errors ?: emptyList())
            .map { Problem("Import error: $name", it.message) } +
        listOfNotNull(importerError)
            .map { Problem("Import error: $name", importerError?.message ?: "Unknown error.") }

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
        adapter.createEntityGroupVisualizer(this)
}
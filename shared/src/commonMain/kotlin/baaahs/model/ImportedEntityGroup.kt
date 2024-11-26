package baaahs.model

import baaahs.device.FixtureType
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.mapper.MappingSession
import baaahs.sim.FixtureSimulation
import baaahs.sm.webapi.Problem
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.entity.ItemVisualizer

class ImportedEntityGroup(
    override val name: String,
    override val description: String? = null,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    private var importerResults: Importer.Results?,
    private var importerError: Exception?,
    override val locator: EntityLocator = EntityLocator.next()
) : Model.BaseEntity(), Model.EntityGroup {
    override val fixtureType: FixtureType get() = PixelArrayDevice // TODO


    override val bounds: Pair<Vector3F, Vector3F> = boundingBox(importerResults?.vertices ?: emptyList())
    override val entities: List<Model.Entity> = importerResults?.entities ?: emptyList()
    // .map { it.transform(transformation) }

    override val problems: Collection<Problem> =
        (importerResults?.errors ?: emptyList())
            .map { Problem("Import error: $name", it.message) } +
        listOfNotNull(importerError)
            .map { Problem("Import error: $name", importerError?.message ?: "Unknown error.") }

    override fun createFixtureSimulation(adapter: EntityAdapter): FixtureSimulation =
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
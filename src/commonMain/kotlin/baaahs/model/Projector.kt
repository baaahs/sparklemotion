package baaahs.model

import baaahs.device.FixtureType
import baaahs.device.ProjectorDevice
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.gl.Mode
import baaahs.sim.FixtureSimulation
import baaahs.sim.ProjectorSimulation
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.ItemVisualizer
import kotlinx.serialization.Transient

class Projector(
    override val name: String,
    override val description: String? = null,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    val monitorName: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    @Transient override val id: EntityId = Model.Entity.nextId(),
) : Model.BaseEntity() {
    override val fixtureType: FixtureType
        get() = ProjectorDevice

    override val bounds: Pair<Vector3F, Vector3F>
        get() = position to position

    val mode: Mode?
        get() = if (width != null && height != null) Mode(width, height) else null

    override fun createFixtureSimulation(adapter: EntityAdapter): FixtureSimulation =
        ProjectorSimulation(this, adapter)

    override fun createVisualizer(adapter: EntityAdapter): ItemVisualizer<out Model.Entity> =
        adapter.createProjectorVisualizer(this)
}
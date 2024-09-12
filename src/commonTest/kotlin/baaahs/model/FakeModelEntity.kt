package baaahs.model

import baaahs.device.FixtureType
import baaahs.device.PixelArrayDevice
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.sim.FakeFixtureSimulation
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.FakeItemVisualizer
import baaahs.visualizer.entity.ItemVisualizer

open class FakeModelEntity(
    override val name: String,
    override val fixtureType: FixtureType = PixelArrayDevice,
    override val description: String = name,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    override val id: EntityId = Model.Entity.nextId()
) : Model.BaseEntity() {
    override val bounds: Pair<Vector3F, Vector3F>
        get() = Vector3F.origin to Vector3F.origin

    override fun createFixtureSimulation(adapter: EntityAdapter) =
        FakeFixtureSimulation(FakeItemVisualizer(this))

    override fun createVisualizer(adapter: EntityAdapter): ItemVisualizer<Model.Entity> = TODO("not implemented")
}
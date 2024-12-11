@file:OptIn(ExperimentalSerializationApi::class)

package baaahs.model

import baaahs.device.FixtureType
import baaahs.device.PixelArrayDevice
import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.scene.EntityEditorPanel
import baaahs.scene.MutableEntity
import baaahs.sim.FakeFixtureSimulation
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.FakeItemVisualizer
import baaahs.visualizer.entity.ItemVisualizer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi

class FakeModelEntity(
    override val name: String,
    override val fixtureType: FixtureType = PixelArrayDevice,
    override val description: String? = null,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    override val locator: EntityLocator = EntityLocator.next()
) : Model.BaseEntity() {
    override val bounds: Pair<Vector3F, Vector3F>
        get() = Vector3F.origin to Vector3F.origin

    override fun createFixtureSimulation(adapter: EntityAdapter) =
        FakeFixtureSimulation(FakeItemVisualizer(this))

    override fun createVisualizer(adapter: EntityAdapter): ItemVisualizer<Model.Entity> = TODO("not implemented")
}

class FakeModelEntityData(
    override val title: String,
    val fixtureType: FixtureType = PixelArrayDevice,
    override val description: String? = null,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val locator: EntityLocator = EntityLocator.next()
) : EntityData {
    override fun edit(): MutableEntity =
        MutableFakeModelEntity(title, fixtureType, description, position, rotation, scale, locator)

    override fun open(parentTransformation: Matrix4F): Model.Entity =
        FakeModelEntity(title, fixtureType, description, position, rotation, scale, locator)

    override fun open(
        position: Vector3F,
        rotation: EulerAngle,
        scale: Vector3F
    ): Model.Entity {
        TODO("not implemented")
    }
}

class MutableFakeModelEntity(
    name: String,
    var fixtureType: FixtureType = PixelArrayDevice,
    description: String? = null,
    position: Vector3F = Vector3F.origin,
    rotation: EulerAngle = EulerAngle.identity,
    scale: Vector3F = Vector3F.unit3d,
    id: EntityLocator = EntityLocator.next()
) : MutableEntity(
    name, description, position, rotation, scale, id
) {
    override val typeTitle: String get() = "Fake Model Entity"

    override fun build(): EntityData =
        FakeModelEntityData(title, fixtureType, description, position, rotation, scale, locator)

    override fun getEditorPanels(): List<EntityEditorPanel<out MutableEntity>> = TODO("not implemented")
}

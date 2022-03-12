package baaahs.model

import baaahs.device.FixtureType
import baaahs.device.PixelArrayDevice
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.sim.FixtureSimulation
import baaahs.sim.LightBarSimulation
import baaahs.sim.SimulationEnv
import baaahs.visualizer.EntityAdapter
import kotlinx.serialization.Transient

class LightBar(
    override val name: String,
    override val description: String? = null,
    override val position: Vector3F = Vector3F.origin, // TODO: Represent using transformation translation.
    override val rotation: EulerAngle = EulerAngle.identity, // TODO: Represent using transformation rotation (and scale?) ... or add length?
    override val scale: Vector3F = Vector3F.unit3d,
    val startVertex: Vector3F,
    val endVertex: Vector3F,
    @Transient override val id: EntityId = Model.Entity.nextId()
) : Model.BaseEntity(), LinearPixelArray {
    override val fixtureType: FixtureType
        get() = PixelArrayDevice

    override val bounds: Pair<Vector3F, Vector3F>
        get() = startVertex to endVertex

    val length: Float // TODO: Derive from transformation scale?
        get() = (endVertex - startVertex).length()

    /**
     * Since a light bar presumably has pixels at both ends, the first and last pixels
     * are at [startVertex] and [endVertex] respectively.
     */
    override fun calculatePixelLocalLocation(index: Int, count: Int): Vector3F {
        val delta = endVertex - startVertex
        return delta * index.toDouble() / (count - 1).toDouble() + startVertex
    }

    override fun createFixtureSimulation(simulationEnv: SimulationEnv, adapter: EntityAdapter): FixtureSimulation =
        LightBarSimulation(this, simulationEnv, adapter)

    override fun createVisualizer(adapter: EntityAdapter) =
        adapter.createLightBarVisualizer(this)
}

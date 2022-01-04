package baaahs.model

import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.sim.FixtureSimulation
import baaahs.sim.LightBarSimulation
import baaahs.sim.SimulationEnv
import baaahs.visualizer.EntityVisualizer
import baaahs.visualizer.visualizerBuilder

class LightBar(
    override val name: String,
    override val description: String?,
    val startVertex: Vector3F, // TODO: Represent using transformation translation.
    val endVertex: Vector3F, // TODO: Represent using transformation rotation (and scale?) ... or add length?
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
) : Model.BaseEntity(), LinearPixelArray {
    override val deviceType: DeviceType
        get() = PixelArrayDevice

    override val bounds: Pair<Vector3F, Vector3F>
        get() = startVertex to endVertex

    val length: Float // TODO: Derive from transformation scale?
        get() = (endVertex - startVertex).length()

    /**
     * Since a light bar presumably has pixels at both ends, the first and last pixels
     * are at [startVertex] and [endVertex] respectively.
     */
    override fun calculatePixelLocation(index: Int, count: Int): Vector3F {
        val delta = endVertex - startVertex
        return delta * index.toDouble() / (count - 1).toDouble() + startVertex
    }

    override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
        LightBarSimulation(this, simulationEnv)

    override fun createVisualizer(simulationEnv: SimulationEnv): EntityVisualizer =
        visualizerBuilder.createLightBarVisualizer(this, simulationEnv)
}

package baaahs.model

import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.sim.FixtureSimulation
import baaahs.sim.LightBarSimulation
import baaahs.sim.SimulationEnv

class LightBar(
    override val name: String,
    override val description: String,
    val startVertex: Vector3F,
    val endVertex: Vector3F
) : Model.Entity, LinearPixelArray {
    override val deviceType: DeviceType
        get() = PixelArrayDevice

    override val bounds: Pair<Vector3F, Vector3F>
        get() = startVertex to endVertex

    val length: Float
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
}

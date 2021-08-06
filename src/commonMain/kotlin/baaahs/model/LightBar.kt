package baaahs.model

import baaahs.device.DeviceType
import baaahs.geom.Vector3F
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.sim.FixtureSimulation
import baaahs.sim.LightBarSimulation
import baaahs.sim.SimulationEnv

class LightBar(
    override val name: String,
    override val description: String,
    override val deviceType: DeviceType,
    val startVertex: Vector3F,
    val endVertex: Vector3F
) : Model.Entity {
    override val modelBounds: Pair<Vector3F, Vector3F>
        get() = startVertex to endVertex

    val length: Float
        get() = startVertex.let { (endVertex - it).length() }

    fun getPixelLocations(pixelCount: Int): List<Vector3F> {
        return LinearSurfacePixelStrategy()
            .betweenPoints(startVertex, endVertex, pixelCount)
    }

    override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
        LightBarSimulation(this, simulationEnv)
}
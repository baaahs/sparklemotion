package baaahs.model

import baaahs.fixtures.DeviceType
import baaahs.geom.Vector3F
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.sim.FixtureSimulation
import baaahs.sim.LightBarSimulation
import baaahs.sim.SimulationEnv

class LightBar(
    override val name: String,
    override val description: String,
    override val deviceType: DeviceType,
    val startVertex: Vector3F?,
    val endVertex: Vector3F?
) : Model.Entity {
    val length: Float?
        get() = startVertex?.let { endVertex?.minus(it)?.length() }

    fun getPixelLocations(pixelCount: Int): List<Vector3F> {
        return LinearSurfacePixelStrategy()
            .betweenPoints(
                startVertex ?: error("no start vertex!"),
                endVertex ?: error("no end vertex!"),
                pixelCount
            )
    }

    override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
        LightBarSimulation(this, simulationEnv)
}
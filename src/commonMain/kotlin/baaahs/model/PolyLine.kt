package baaahs.model

import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.sim.FixtureSimulation
import baaahs.sim.LightBarSimulation
import baaahs.sim.SimulationEnv

class PolyLine(
    override val name: String,
    override val description: String,
    val segments: List<Segment>
) : Model.Entity, PlacedPixelArray {
    override val deviceType: DeviceType
        get() = PixelArrayDevice

    override val bounds: Pair<Vector3F, Vector3F>
        by lazy { boundingBox(segments.flatMap { listOf(it.startVertex, it.endVertex) }) }

    val length: Float
        get() = segments.map { it.length }.sum()

    val pixelCount: Int = segments.sumOf { it.pixelCount }

    override fun calculatePixelLocations(expectedPixelCount: Int): List<Vector3F> {
        return segments.flatMap { segment -> segment.calculatePixelLocations() }
    }

    override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
        LightBarSimulation(this, simulationEnv)

    data class Segment(
        val startVertex: Vector3F,
        val endVertex: Vector3F,
        val pixelCount: Int
    ) {
        val length: Float get() = (endVertex - startVertex).length()

        fun calculatePixelLocations(): List<Vector3F> {
            return (0 until pixelCount).map { i ->
                calculatePixelLocation(i, pixelCount)
            }
        }

        /**
         * Since a light bar presumably has pixels at both ends, the first and last pixels
         * are at [startVertex] and [endVertex] respectively.
         */
        fun calculatePixelLocation(index: Int, count: Int): Vector3F {
            val delta = endVertex - startVertex
            return delta * index.toDouble() / (count - 1).toDouble() + startVertex
        }
    }
}

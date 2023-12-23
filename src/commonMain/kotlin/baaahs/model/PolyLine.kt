package baaahs.model

import baaahs.device.FixtureType
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.FixtureOptions
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.sim.FixtureSimulation
import baaahs.sim.simulations
import baaahs.visualizer.EntityAdapter
import kotlinx.serialization.Transient

class Grid(
    name: String,
    description: String? = null,
    position: Vector3F = Vector3F.origin,
    rotation: EulerAngle = EulerAngle.identity,
    scale: Vector3F = Vector3F.unit3d,
    rows: Int,
    columns: Int,
    rowGap: Float,
    columnGap: Float,
    direction: GridData.Direction,
    zigZag: Boolean,
    stagger: Int = 1,
    id: EntityId = Model.Entity.nextId()
): PolyLine(
    name, description,
    calcSegments(rows, columns, rowGap, columnGap, direction, zigZag, stagger),
    position, rotation, scale, columnGap, rowGap, id) {
}

fun calcSegments(
    rows: Int,
    columns: Int,
    rowGap: Float,
    columnGap: Float,
    direction: GridData.Direction,
    zigZag: Boolean,
    stagger: Int = 1
): List<PolyLine.Segment> {
    return when (direction) {
        GridData.Direction.RowsThenColumns ->
            (0 until rows).map { yI ->
                val staggerAmount = columnGap / stagger * (yI % stagger)
                val y = yI * rowGap
                PolyLine.Segment(
                    Vector3F(0f + staggerAmount, y, 0f),
                    Vector3F((columns - 1) * columnGap + staggerAmount, y, 0f),
                    columns
                ).let {
                    if (zigZag && yI % 2 == 1) it.reverse() else it
                }
            }
        GridData.Direction.ColumnsThenRows ->
            (0 until columns).map { xI ->
                val staggerAmount = rowGap / stagger * (xI % stagger)
                val x = xI * columnGap
                PolyLine.Segment(
                    Vector3F(x, 0f + staggerAmount, 0f),
                    Vector3F(x, (rows - 1) * rowGap + staggerAmount, 0f),
                    rows
                ).let {
                    if (zigZag && xI % 2 == 1) it.reverse() else it
                }
            }
    }
}

open class PolyLine(
    override val name: String,
    override val description: String? = null,
    val segments: List<Segment>,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    val xPadding: Float,
    val yPadding: Float,
    @Transient override val id: EntityId = Model.Entity.nextId()
) : Model.BaseEntity(), PlacedPixelArray {
    override val defaultFixtureOptions: FixtureOptions?
        get() = PixelArrayDevice.Options(pixelCount, pixelArrangement = LinearSurfacePixelStrategy())
    override val fixtureType: FixtureType
        get() = PixelArrayDevice

    override val bounds: Pair<Vector3F, Vector3F>
        by lazy { boundingBox(segments.flatMap { listOf(it.startVertex, it.endVertex) }) }

    val length: Float
        get() = segments.map { it.length }.sum()

    val pixelCount: Int = segments.sumOf { it.pixelCount }

    override fun calculatePixelLocalLocations(expectedPixelCount: Int): List<Vector3F> {
        return segments.flatMap { segment -> segment.calculatePixelLocations() }
    }

    override fun createFixtureSimulation(adapter: EntityAdapter): FixtureSimulation =
        simulations.forLightBar(this, adapter)

    override fun createVisualizer(adapter: EntityAdapter) =
        adapter.createPolyLineVisualizer(this)

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
        private fun calculatePixelLocation(index: Int, count: Int): Vector3F {
            val delta = endVertex - startVertex
            return delta * index.toDouble() / (count - 1).toDouble() + startVertex
        }

        fun reverse(): Segment = Segment(endVertex, startVertex, pixelCount)
    }
}

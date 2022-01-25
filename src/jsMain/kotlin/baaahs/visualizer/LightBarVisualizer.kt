package baaahs.visualizer

import baaahs.geom.Vector3F
import baaahs.model.LightBar
import baaahs.model.Model
import baaahs.model.PixelArray
import baaahs.model.PolyLine
import baaahs.sim.SimulationEnv
import baaahs.util.three.addPadding
import three.js.*
import three_ext.clear

class LightBarVisualizer(
    lightBar: LightBar,
    simulationEnv: SimulationEnv,
    vizPixels: VizPixels? = null
) : PixelArrayVisualizer<LightBar>(lightBar, simulationEnv, vizPixels) {
    init { update(entity) }

    // TODO!!!
    val pixelCount_UNKNOWN_BUSTED = 100

    override fun isApplicable(newEntity: Model.Entity): LightBar? =
        newEntity as? LightBar

    override fun getPixelLocations(): List<Vector3F> =
        entity.calculatePixelLocalLocations(pixelCount_UNKNOWN_BUSTED)

    override fun getSegments(): List<PolyLine.Segment> =
        listOf(PolyLine.Segment(entity.startVertex, entity.endVertex, pixelCount_UNKNOWN_BUSTED))
}

class PolyLineVisualizer(
    polyLine: PolyLine,
    simulationEnv: SimulationEnv
) : PixelArrayVisualizer<PolyLine>(polyLine, simulationEnv) {
    init { update(entity) }

    override fun isApplicable(newEntity: Model.Entity): PolyLine? =
        newEntity as? PolyLine

    override fun getPixelLocations(): List<Vector3F> =
        entity.segments.flatMap {
            it.calculatePixelLocations()
        }

    override fun getSegments(): List<PolyLine.Segment> =
        entity.segments

    override fun addPadding(container: Box3, newEntity: PolyLine) {
        container.min.x -= entity.xPadding
        container.max.x += entity.xPadding
        container.min.y -= entity.yPadding
        container.max.y += entity.yPadding
    }
}

abstract class PixelArrayVisualizer<T : PixelArray>(
    pixelArray: T,
    simulationEnv: SimulationEnv,
    vizPixels: VizPixels? = null
) : BaseEntityVisualizer<T>(pixelArray) {
    //    private val mesh: Mesh<BoxGeometry, MeshBasicMaterial> = Mesh()
    private val containerMaterial = LineDashedMaterial()
    private val containerBox = Box3()
    private val container = Box3Helper(containerBox).apply { material = containerMaterial }

    private val strandsGroup = Group()
    private val strandsMaterial = LineDashedMaterial()

    private val pixelsGeometry = BufferGeometry()
    private val pixelsMaterial = PointsMaterial()
    private val pixels = Points(pixelsGeometry, pixelsMaterial)

    override val obj = Group().apply {
        add(container)
        add(strandsGroup)
        add(pixels)
        vizPixels?.addTo(VizObj(this))
    }

    abstract fun getPixelLocations(): List<Vector3F>
    abstract fun getSegments(): List<PolyLine.Segment>

    override fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToLine(containerMaterial)
        strandsGroup.children.forEach {
            it as ArrowHelper
            entityStyle.applyToLine(it.line.material, EntityStyle.Use.Strand)
            entityStyle.applyToMesh(it.cone.material, EntityStyle.Use.Strand)
        }
        entityStyle.applyToPoints(pixelsMaterial)
    }

    open fun addPadding(container: Box3, newEntity: T) {
        container.addPadding(.02)
    }

    override fun update(newEntity: T, callback: ((EntityVisualizer<*>) -> Unit)?) {
        super.update(newEntity, callback)

        val pixelLocations = getPixelLocations().map { it.toVector3() }.toTypedArray()
        containerBox.setFromPoints(pixelLocations)
        addPadding(containerBox, newEntity)

        strandsGroup.clear()
        getSegments().forEach { segment ->
            val vector = segment.endVertex - segment.startVertex
            val normal = vector.normalize()
            val length = vector.length()
            strandsGroup.add(
                ArrowHelper(
                    normal.toVector3(),
                    segment.startVertex.toVector3(),
                    length,
                    0x228822,
                    length / segment.pixelCount,
                    length / segment.pixelCount
                )
            )
        }

        pixelsGeometry.setFromPoints(pixelLocations)
    }
}

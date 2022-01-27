package baaahs.visualizer

import baaahs.geom.Vector3F
import baaahs.model.LightBar
import baaahs.model.Model
import baaahs.model.PixelArray
import baaahs.model.PolyLine
import baaahs.util.three.addPadding
import three.js.*
import three_ext.clear
import three_ext.set

class LightBarVisualizer(
    lightBar: LightBar,
    adapter: EntityAdapter,
    vizPixels: VizPixels? = null
) : PixelArrayVisualizer<LightBar>(lightBar, vizPixels) {
    init { update(entity) }

    // TODO!!!
    val pixelCount_UNKNOWN_BUSTED = 100

    override fun isApplicable(newEntity: Model.Entity): LightBar? =
        newEntity as? LightBar

    override fun getPixelLocations(): List<Vector3F> =
        entity.calculatePixelLocalLocations(pixelCount_UNKNOWN_BUSTED)

    override fun getSegments(): List<PolyLine.Segment> =
        listOf(PolyLine.Segment(entity.startVertex, entity.endVertex, pixelCount_UNKNOWN_BUSTED))

    override fun updateContainer(container: Box3Helper, pixelLocations: Array<Vector3>) {
        val vector = entity.endVertex - entity.startVertex
        val center = entity.startVertex + vector / 2f
        val length = vector.length()
        val normal = vector.normalize()

        container.box.setFromPoints(
            arrayOf(
                Vector3(-.5, -.5, -length / 2f + .5),
                Vector3(.5, .5, length / 2f + .5)
            )
        )

        val quaternion = Quaternion().setFromUnitVectors(
            Vector3F.facingForward.toVector3(),
            normal.toVector3()
        )
        container.position.set(center)
        container.rotation.setFromQuaternion(quaternion)
        container.scale.set(Vector3F.unit3d)
    }
}

class PolyLineVisualizer(
    polyLine: PolyLine
) : PixelArrayVisualizer<PolyLine>(polyLine) {
    init { update(entity) }

    override fun isApplicable(newEntity: Model.Entity): PolyLine? =
        newEntity as? PolyLine

    override fun getPixelLocations(): List<Vector3F> =
        entity.segments.flatMap {
            it.calculatePixelLocations()
        }

    override fun getSegments(): List<PolyLine.Segment> =
        entity.segments

    override fun addPadding(container: Box3Helper) {
        container.box.min.x -= entity.xPadding
        container.box.max.x += entity.xPadding
        container.box.min.y -= entity.yPadding
        container.box.max.y += entity.yPadding
    }
}

abstract class PixelArrayVisualizer<T : PixelArray>(
    pixelArray: T,
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
        vizPixels?.addTo(this)
    } as Object3D

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

    open fun addPadding(container: Box3Helper) {
        container.box.addPadding(.02)
    }

    open fun updateContainer(container: Box3Helper, pixelLocations: Array<Vector3>) {
        container.box.setFromPoints(pixelLocations)
        addPadding(container)
    }

    override fun update(newEntity: T, callback: ((EntityVisualizer<*>) -> Unit)?) {
        super.update(newEntity, callback)

        val pixelLocations = getPixelLocations().map { it.toVector3() }.toTypedArray()
        updateContainer(container, pixelLocations)

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

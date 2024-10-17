package baaahs.visualizer.entity

import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.model.LightBar
import baaahs.model.ModelUnit
import baaahs.model.PixelArray
import baaahs.model.PolyLine
import baaahs.util.three.addPadding
import baaahs.visualizer.*
import three.*
import three_ext.toVector3F
import kotlin.math.abs

class Container(
    val box: Box3,
    val units: ModelUnit,
    val position: Vector3? = null,
    val rotation: Quaternion? = null,
    val scale: Vector3? = null,
    val isCentered: Boolean = false
) {
    val min = box.min.toVector3F()
    val max = box.max.toVector3F()
    val size = max - min
    val offset = (size * .5 + min).toVector3()

    fun createOutline(material: LineDashedMaterial) =
        Box3Helper(box).apply {
            this.material = material
            applyTransforms(this)
            position.add(offset)
        }

    fun createMesh(material: MeshBasicMaterial): Mesh<BoxGeometry, MeshBasicMaterial> {
        val geom = BoxGeometry(size.x, size.y, size.z)

        return Mesh(geom, material).apply {
            applyTransforms(this)
            if (isCentered)
                position.add(offset)
        }
    }

    private fun applyTransforms(obj: Object3D) {
        position?.let { obj.position.copy(it) }
        rotation?.let { obj.rotation.setFromQuaternion(it) }
        scale?.let { obj.scale.copy(it) }
    }
}

class LightBarVisualizer(
    lightBar: LightBar,
    adapter: EntityAdapter,
    vizPixels: VizPixels? = null
) : PixelArrayVisualizer<LightBar>(lightBar, vizPixels) {
    private val units = adapter.units
    private val borderWidth = units.fromCm(1.25)

    init {
        update(item)
    }

    override fun isApplicable(newItem: Any): LightBar? =
        newItem as? LightBar

    override fun getPixelLocations(): List<Vector3F> =
        item.calculatePixelLocalLocations(pixelCount_UNKNOWN_BUSTED)

    override fun getSegments(): List<PolyLine.Segment> =
        listOf(PolyLine.Segment(item.startVertex, item.endVertex, pixelCount_UNKNOWN_BUSTED))

    override fun addPadding(box: Box3) {
        box.addPadding(.02)
    }

    override fun calculateContainer(pixelLocations: Array<Vector3>): Container {
        val box = Box3()
        val vector = item.endVertex - item.startVertex
        val center = item.startVertex + vector / 2f
        val length = vector.length()
        val normal = vector.normalize()

        box.setFromPoints(
            arrayOf(
                Vector3(-borderWidth, -borderWidth, -length / 2f + borderWidth),
                Vector3(borderWidth, borderWidth, length / 2f + borderWidth)
            )
        )
        box.translate(center.toVector3())
        box.translate(Vector3(0, 0, -borderWidth))

        val quaternion = Quaternion().setFromUnitVectors(
            Vector3F.facingForward.toVector3(),
            normal.toVector3()
        )

        return Container(box, units, center.toVector3(), quaternion)
    }

    companion object {
        // TODO!!!
        const val pixelCount_UNKNOWN_BUSTED = 100
    }
}

class PolyLineVisualizer(
    polyLine: PolyLine, adapter: EntityAdapter, vizPixels: VizPixels?
) : PixelArrayVisualizer<PolyLine>(polyLine, vizPixels) {
    private val units = adapter.units

    init {
        update(item)
    }

    override fun isApplicable(newItem: Any): PolyLine? =
        newItem as? PolyLine

    override fun getPixelLocations(): List<Vector3F> =
        item.segments.flatMap {
            it.calculatePixelLocations()
        }

    override fun getSegments(): List<PolyLine.Segment> =
        item.segments

    override fun addPadding(box: Box3) {
        box.min.x -= abs(item.xPadding)
        box.max.x += abs(item.xPadding)
        box.min.y -= abs(item.yPadding)
        box.max.y += abs(item.yPadding)
    }

    override fun calculateContainer(pixelLocations: Array<Vector3>): Container {
        val box = Box3()
        box.setFromPoints(pixelLocations)
        addPadding(box)
        return Container(box, units, isCentered = true)
    }
}

abstract class PixelArrayVisualizer<T : PixelArray>(
    pixelArray: T,
    private val vizPixels: VizPixels? = null
) : BaseEntityVisualizer<T>(pixelArray) {
    //    private val mesh: Mesh<BoxGeometry, MeshBasicMaterial> = Mesh()
    private val containerOutlineMaterial = EntityStyle.lineMaterial()
    private val containerMaterial = EntityStyle.meshMaterial()

    private val strandGroup = Group()
    private val strandMaterial = EntityStyle.lineMaterial()
    private val strandHintMaterial = EntityStyle.meshMaterial()

    private val pixelsPreview = PixelsPreview()

    override val obj = Group()

    abstract fun getPixelLocations(): List<Vector3F>
    abstract fun getSegments(): List<PolyLine.Segment>

    override fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToLine(containerOutlineMaterial, EntityStyle.Use.BacklitSurface)
        entityStyle.applyToMesh(containerMaterial, EntityStyle.Use.BacklitSurface)

        entityStyle.applyToLine(strandMaterial, EntityStyle.Use.LightStrand)
        entityStyle.applyToMesh(strandHintMaterial, EntityStyle.Use.LightStrandHint)

        pixelsPreview.applyStyle(entityStyle)
    }

    abstract fun addPadding(box: Box3)

    abstract fun calculateContainer(pixelLocations: Array<Vector3>): Container

    override fun update(newItem: T) {
        super.update(newItem)

        val pixelLocations = getPixelLocations().map { it.toVector3() }.toTypedArray()
        val container = calculateContainer(pixelLocations)
        val containerOutline = container.createOutline(containerOutlineMaterial)
        val containerMesh = container.createMesh(containerMaterial)

        obj.clear()
        obj.add(containerOutline)
        obj.add(containerMesh)
        obj.add(strandGroup)
        obj.add(pixelsPreview.points)
        vizPixels?.addTo(obj)

        strandGroup.clear()
        var lastEndVertex: Vector3F? = null
        getSegments().forEach { segment ->
            val vector = segment.endVertex - segment.startVertex
            val length = vector.length()
            val arrowSize = length / segment.pixelCount * .8f

            lastEndVertex?.let { lastEndVertex ->
                val connectorVector = segment.startVertex - lastEndVertex
                val connectorLength = connectorVector.length()
                strandGroup.add(
                    ArrowHelper(
                        connectorVector.normalize().toVector3(),
                        lastEndVertex.toVector3(),
                        connectorLength,
                        0x228822,
                        arrowSize,
                        arrowSize
                    ).apply {
                        line.asDynamic().material = strandMaterial
                        cone.asDynamic().material = strandHintMaterial
                    }
                )
            }

            strandGroup.add(
                ArrowHelper(
                    vector.normalize().toVector3(),
                    segment.startVertex.toVector3(),
                    length,
                    0x228822,
                    arrowSize,
                    arrowSize
                ).apply {
                    line.asDynamic().material = strandMaterial
                    cone.asDynamic().material = strandHintMaterial
                }
            )
            lastEndVertex = segment.endVertex
        }

        pixelsPreview.setLocations(pixelLocations)
    }

    override fun receiveRemoteFrameData(reader: ByteArrayReader) {
        vizPixels?.readColors(reader)
    }
}

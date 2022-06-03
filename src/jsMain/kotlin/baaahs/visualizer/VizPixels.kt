package baaahs.visualizer

import baaahs.Color
import baaahs.device.PixelFormat
import baaahs.geom.Matrix4F
import baaahs.geom.Vector2
import baaahs.io.ByteArrayReader
import baaahs.resourcesBase
import baaahs.sm.brain.proto.Pixels
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import three.js.*
import three_ext.*
import three_ext.Matrix4
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class VizPixels(
    val positions: Array<Vector3>,
    val normal: Vector3,
    val transformation: Matrix4F,
    val pixelFormat: PixelFormat,
    val pixelSizeRange: ClosedFloatingPointRange<Float> = 2f..5f
) : Pixels {
    override val size = positions.size
    private val pixGeometry = BufferGeometry()
    private val planeGeometry: BufferGeometry
    private val vertexColorBufferAttr: Float32BufferAttribute
    private val colorsAsInts = IntArray(size) // store colors as an int array too for Pixels.get()

    init {
        val positionsArray = Float32Array(size * 3)
        positions.forEachIndexed { i, v ->
            positionsArray[i * 3] = v.x.toFloat()
            positionsArray[i * 3 + 1] = v.y.toFloat()
            positionsArray[i * 3 + 2] = v.z.toFloat()
        }

        val positionsBufferAttr = Float32BufferAttribute(positionsArray, 3)
        pixGeometry.setAttribute("position", positionsBufferAttr)

        vertexColorBufferAttr = Float32BufferAttribute(Float32Array(size * 3 * 4), 3)
        vertexColorBufferAttr.usage = DynamicDrawUsage

        val rotator = Rotator(vector3FacingForward, normal)
        planeGeometry = BufferGeometryUtils.mergeBufferGeometries(positions.map { position ->
            val pixelWidth = pixelSizeRange.interpolate(Random.nextFloat())
            val pixelHeight = pixelSizeRange.interpolate(Random.nextFloat())
            PlaneBufferGeometry(pixelWidth, pixelHeight).apply {
                rotator.rotate(this)
                translate(position.x, position.y, position.z)
            }
        }.toTypedArray())
        planeGeometry.setAttribute("color", vertexColorBufferAttr)
    }

    fun ClosedFloatingPointRange<Float>.interpolate(fl: Float) =
        fl * (endInclusive - start) + start

    private val pixelsMesh = Mesh(planeGeometry, MeshBasicMaterial().apply {
        side = FrontSide
        transparent = true
        blending = AdditiveBlending
//            depthFunc = AlwaysDepth
        depthTest = false
        depthWrite = false
        vertexColors = true

        map = roundLightTx
    })

    fun addTo(parent: VizObj) {
        parent.add(pixelsMesh)
    }

    fun removeFrom(parent: VizObj) {
        parent.remove(pixelsMesh)
    }

    override fun get(i: Int): Color {
        return Color(colorsAsInts[i])
    }

    override fun set(i: Int, color: Color) {
        colorsAsInts[i] = color.argb

        val redF = color.redF / 2
        val greenF = color.greenF / 2
        val blueF = color.blueF / 2

        set(i, redF, greenF, blueF)
    }

    private fun set(i: Int, redF: Float, greenF: Float, blueF: Float) {
        val rgb3Buf = vertexColorBufferAttr
        rgb3Buf.setXYZ(i * 4, redF, greenF, blueF)
        rgb3Buf.setXYZ(i * 4 + 1, redF, greenF, blueF)
        rgb3Buf.setXYZ(i * 4 + 2, redF, greenF, blueF)
        rgb3Buf.setXYZ(i * 4 + 3, redF, greenF, blueF)
        vertexColorBufferAttr.needsUpdate = true
    }

    override fun set(colors: Array<Color>) {
        val maxCount = min(this.size, colors.size)
        val rgbBuf = vertexColorBufferAttr.array
        for (i in 0 until maxCount) {
            colorsAsInts[i] = colors[i].argb

            val pColor = colors[i]
            rgbBuf[i * 3] = pColor.redF / 2
            rgbBuf[i * 3 + 1] = pColor.greenF / 2
            rgbBuf[i * 3 + 2] = pColor.blueF / 2
        }
        vertexColorBufferAttr.needsUpdate = true
    }

    fun getPixelLocationsInModelSpace(): Array<Vector3> = positions

    fun getPixelLocationsInPanelSpace(surfaceVisualizer: SurfaceVisualizer): Array<Vector2> {
        val panelGeom = surfaceVisualizer.geometry.clone()
        val pixGeom = pixGeometry.clone()

        val facingForward = vector3FacingForward

        // Rotate to straight on.
        val rotator = Rotator(surfaceVisualizer.panelNormal, facingForward)
        rotator.rotate(panelGeom)
        rotator.rotate(pixGeom)

        // Translate and scale pixels to panel space (0f..1f)
        panelGeom.computeBoundingBox()
        val boundingBox = panelGeom.boundingBox!!
        val min = boundingBox.min
        val size = boundingBox.max - boundingBox.min

        val translate = Matrix4().makeTranslation(-min.x, -min.y, -min.z)
        panelGeom.applyMatrix4(translate)
        pixGeom.applyMatrix4(translate)

        val scale = Matrix4().makeScale(1.0 / size.x, 1.0 / size.y, 1.0)
        panelGeom.applyMatrix4(scale)
        pixGeom.applyMatrix4(scale)

        val pixelVs = mutableListOf<Vector2>()
        val pixelPositions = pixGeom.getAttribute("position")
        val array = pixelPositions.array as Float32Array
        for (i in 0 until pixelPositions.count * 3 step 3) {
            val v = Vector2(clamp(array[i]).toDouble(), clamp(array[i + 1]).toDouble())
            pixelVs.add(v)
        }

        return pixelVs.toTypedArray()
    }

    fun clamp(f: Float): Float = min(1f, max(f, 0f))

    fun readColors(reader: ByteArrayReader) {
        val pixelCount = reader.readInt()
        val minPixCount = min(size, pixelCount)
        for (i in 0 until minPixCount) {
            pixelFormat.readColor(reader) { r, g, b ->
                set(i, r, g, b)
            }
        }
    }

    companion object {
        private val roundLightTx = TextureLoader().load(
            "$resourcesBase/visualizer/textures/round.png",
            { println("loaded!") },
            { println("progress!") },
            { println("error!") }
        )
    }
}

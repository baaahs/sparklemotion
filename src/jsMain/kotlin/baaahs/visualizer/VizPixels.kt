package baaahs.visualizer

import baaahs.Color
import baaahs.device.PixelFormat
import baaahs.device.PixelLocations
import baaahs.geom.Matrix4F
import baaahs.geom.Vector2
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.resourcesBase
import baaahs.sm.brain.proto.Pixels
import baaahs.util.globalLaunch
import kotlinx.coroutines.delay
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import three.examples.jsm.utils.mergeGeometries
import three.js.*
import three_ext.minus
import three_ext.vector3FacingForward
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class VizPixels(
    private val positions: Array<Vector3>,
    val normal: Vector3,
    val transformation: Matrix4F,
    private val pixelFormat: PixelFormat,
    private val pixelSizeRange: ClosedFloatingPointRange<Float>,
    private val bothSides: Boolean = false
) : Pixels {
    override val size = positions.size
    private val vertexColorBufferAttr =
        Float32BufferAttribute(Float32Array(size * 3 * 4).buffer, 3).apply {
            usage = DynamicDrawUsage
        }
    private val colorsAsInts = IntArray(size) // store colors as an int array too for Pixels.get()

    private val pixelsMesh = Mesh(BufferGeometry(), MeshBasicMaterial().apply {
        name = "VizPixels"
        side = if (bothSides) DoubleSide else FrontSide
        transparent = true
        opacity = .3333
        blending = AdditiveBlending
//            depthFunc = AlwaysDepth
        depthTest = false
        depthWrite = false
        vertexColors = true

        map = roundLightTx
    })

    // Build plane geometries to represent individual pixels.
    init {
        globalLaunch {
            delay(10.milliseconds)
            val rotator = Rotator(vector3FacingForward, normal)
            val pixelPlaneGeometries = positions.map { position ->
                val pixelWidth = pixelSizeRange.interpolate(Random.nextFloat())
                val pixelHeight = pixelSizeRange.interpolate(Random.nextFloat())
                PlaneGeometry(pixelWidth, pixelHeight).apply {
                    rotator.rotate(this)
                    translate(position.x, position.y, position.z)
                }
            }

            pixelsMesh.geometry =
                mergeGeometries(
                    pixelPlaneGeometries.toTypedArray()
                ).apply {
                    setAttribute("color", vertexColorBufferAttr)
                }
        }
    }

    fun addTo(parent: VizObj) {
        parent.add(pixelsMesh)
    }

    fun removeFrom(parent: VizObj) {
        parent.remove(pixelsMesh)
    }

    override fun get(i: Int): Color =
        Color.from(colorsAsInts[i])

    override fun set(i: Int, color: Color) {
        colorsAsInts[i] = color.argb

        val redF = color.redF
        val greenF = color.greenF
        val blueF = color.blueF

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
            rgbBuf[i * 3] = pColor.redF
            rgbBuf[i * 3 + 1] = pColor.greenF
            rgbBuf[i * 3 + 2] = pColor.blueF
        }
        vertexColorBufferAttr.needsUpdate = true
    }

    fun getPixelLocationsInModelSpace(): Array<Vector3> = positions

    fun getPixelLocationsInPanelSpace(surfaceVisualizer: SurfaceVisualizer): Array<Vector2> {
        val panelGeom = surfaceVisualizer.geometry.clone()

        val pixGeometry = BufferGeometry<NormalOrGLBufferAttributes>()
        val positionsArray = Float32Array(size * 3)
        positions.forEachIndexed { i, v ->
            positionsArray[i * 3] = v.x.toFloat()
            positionsArray[i * 3 + 1] = v.y.toFloat()
            positionsArray[i * 3 + 2] = v.z.toFloat()
        }

        val positionsBufferAttr = Float32BufferAttribute(positionsArray.buffer, 3)
        pixGeometry.setAttribute("position", positionsBufferAttr)

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
        val pixelPositions = pixGeom.getAttribute("position") as Float32BufferAttribute
        val array = pixelPositions.array as Float32Array
        for (i in 0 until array.length step 3) {
            val v = Vector2(clamp(array[i]).toDouble(), clamp(array[i + 1]).toDouble())
            pixelVs.add(v)
        }

        return pixelVs.toTypedArray()
    }

    private fun clamp(f: Float): Float = min(1f, max(f, 0f))

    private fun ClosedFloatingPointRange<Float>.interpolate(fl: Float) =
        fl * (endInclusive - start) + start

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

        // TODO: This is dumb; instead, allow model entities to specify how their pixels appear.
        val undiffusedLedRangeCm: ClosedFloatingPointRange<Float> =
            4f..10f

        val diffusedLedRangeCm: ClosedFloatingPointRange<Float> =
            (2f * 2.54f)..(5f * 2.54f)
    }
}

fun PixelLocations.arrayOfVector3() = Array(size) {
    (get(it) ?: Vector3F.origin).toVector3()
}
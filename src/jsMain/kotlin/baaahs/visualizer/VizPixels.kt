package baaahs.visualizer

import baaahs.Color
import baaahs.Pixels
import baaahs.geom.Vector2
import baaahs.resourcesBase
import info.laht.threekt.THREE
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.loaders.TextureLoader
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Vector3
import info.laht.threekt.math.minus
import info.laht.threekt.objects.Mesh
import info.laht.threekt.scenes.Scene
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import three.BufferGeometryUtils
import three.Float32BufferAttribute
import three.Matrix4
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class VizPixels(vizSurface: VizSurface, val positions: Array<Vector3>) : Pixels {
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
        pixGeometry.addAttribute("position", positionsBufferAttr)

        vertexColorBufferAttr = Float32BufferAttribute(Float32Array(size * 3 * 4), 3)
        vertexColorBufferAttr.dynamic = true

        val rotator = Rotator(Vector3(0, 0, 1), vizSurface.panelNormal)
        planeGeometry = BufferGeometryUtils.mergeBufferGeometries(positions.map { position ->
            val geometry = PlaneBufferGeometry(2 + Random.nextFloat() * 8, 2 + Random.nextFloat() * 8)
            rotator.rotate(geometry)
            geometry.translate(position.x, position.y, position.z)
            geometry
        }.toTypedArray())
        planeGeometry.addAttribute("color", vertexColorBufferAttr)
    }

    private val pixelsMesh = Mesh(planeGeometry, MeshBasicMaterial().apply {
        side = THREE.FrontSide
        transparent = true
        blending = THREE.AdditiveBlending
//            depthFunc = AlwaysDepth
        depthTest = false
        depthWrite = false
        vertexColors = THREE.VertexColors

        map = roundLightTx
    })

    fun addToScene(scene: Scene) {
        scene.add(pixelsMesh)
    }

    fun removeFromScene(scene: Scene) {
        scene.remove(pixelsMesh)
    }

    override fun get(i: Int): Color {
        return Color(colorsAsInts[i])
    }

    override fun set(i: Int, color: Color) {
        colorsAsInts[i] = color.argb

        val redF = color.redF / 2
        val greenF = color.greenF / 2
        val blueF = color.blueF / 2

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

    fun getPixelLocationsInModelSpace(vizSurface: VizSurface): Array<Vector3> = positions

    fun getPixelLocationsInPanelSpace(vizSurface: VizSurface): Array<Vector2> {
        val panelGeom = vizSurface.geometry.clone()
        val pixGeom = pixGeometry.clone()

        val straightOnNormal = Vector3(0, 0, 1)

        // Rotate to straight on.
        val rotator = Rotator(vizSurface.panelNormal, straightOnNormal)
        rotator.rotate(panelGeom)
        rotator.rotate(pixGeom)

        // Translate and scale pixels to panel space (0f..1f)
        panelGeom.computeBoundingBox()
        val boundingBox = panelGeom.boundingBox!!
        val min = boundingBox.min
        val size = boundingBox.max - boundingBox.min

        val translate = Matrix4().makeTranslation(-min.x, -min.y, -min.z)
        panelGeom.applyMatrix(translate)
        pixGeom.applyMatrix(translate)

        val scale = Matrix4().makeScale(1.0 / size.x, 1.0 / size.y, 1.0)
        panelGeom.applyMatrix(scale)
        pixGeom.applyMatrix(scale)

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

    companion object {
        private val roundLightTx = TextureLoader().load(
            "$resourcesBase/visualizer/textures/round.png",
            { println("loaded!") },
            { println("progress!") },
            { println("error!") }
        )
    }
}

package baaahs.visualizer

import baaahs.Color
import baaahs.Pixels
import baaahs.fixtures.ColorResultType
import baaahs.fixtures.IResultBuffer
import baaahs.fixtures.ResultView
import baaahs.fixtures.nuffinBuffer
import baaahs.geom.Vector2
import baaahs.gl.GlContext
import baaahs.gl.render.FixtureRenderTarget
import baaahs.io.ByteArrayReader
import baaahs.only
import baaahs.resourcesBase
import baaahs.ui.nuffin
import com.danielgergely.kgl.*
import kotlinext.js.jsObject
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import three.js.*
import three_ext.*
import three_ext.Matrix4
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

interface PixelsVisualizer {
    fun addTo(scene: VizScene)
    fun removeFrom(scene: VizScene)
}

open class BasePixelsVisualizer(
    val positions: Array<Vector3>,
    val normal: Vector3
) : PixelsVisualizer {
    private val pixGeometry = BufferGeometry()
    protected val planeGeometry: BufferGeometry

    init {
        val positionsArray = Float32Array(positions.size * 3)
        positions.forEachIndexed { i, v ->
            positionsArray[i * 3] = v.x.toFloat()
            positionsArray[i * 3 + 1] = v.y.toFloat()
            positionsArray[i * 3 + 2] = v.z.toFloat()
        }

        val positionsBufferAttr = Float32BufferAttribute(positionsArray, 3)
        pixGeometry.setAttribute("position", positionsBufferAttr)

        val rotator = Rotator(Vector3(0, 0, 1), normal)
        planeGeometry = BufferGeometryUtils.mergeBufferGeometries(positions.map { position ->
            PlaneBufferGeometry(2 + Random.nextFloat() * 8, 2 + Random.nextFloat() * 8).apply {
                rotator.rotate(this)
                translate(position.x, position.y, position.z)
            }
        }.toTypedArray())
    }

//    val material = MeshBasicMaterial().apply {
//        side = FrontSide
//        transparent = true
//        blending = AdditiveBlending
////            depthFunc = AlwaysDepth
//        depthTest = false
//        depthWrite = false
//
//        map = roundLightTx
//        asDynamic().onBeforeCompile = { shader: Shader, renderer: WebGLRenderer ->
//            println("onBeforeCompile:\n$shader")
//        }
//    }
    val material = ShaderMaterial().apply {
        side = FrontSide
        transparent = true
        blending = AdditiveBlending
//            depthFunc = AlwaysDepth
        depthTest = false
        depthWrite = false

        vertexColors = true
        vertexShader = /**language=glsl*/
            """
                attribute float pixelIndex;
                uniform int colorsTextureWidth;
                uniform int pixelOffset;
                uniform sampler2D pixelColorsTexture;
                varying vec4 vColor;
                
                void main() // sm_shader-vertex
                {
                    int pixelI = int(pixelIndex) + pixelOffset;
                    int textureX = pixelI % colorsTextureWidth;
                    int textureY = pixelI / colorsTextureWidth;
                    vColor = texelFetch(pixelColorsTexture, ivec2(textureX, textureY), 0);
                    vec4 modelViewPosition = modelViewMatrix * vec4(position, 1.0);
                    gl_Position = projectionMatrix * modelViewPosition;
                }
            """.trimIndent()

        fragmentShader = /**language=glsl*/
            """
                varying vec4 vColor;
                void main() {  // sm_shader-frag
                    gl_FragColor = vColor;
                }
            """.trimIndent()

        uniforms["colorsTextureWidth"] = jsObject { value = nuffin() }
        uniforms["pixelColorsTexture"] = jsObject { value = nuffin() }
        uniforms["pixelOffset"] = jsObject { value = nuffin() }
//        map = roundLightTx
//        asDynamic().onBeforeCompile = { shader: Shader, renderer: WebGLRenderer ->
//            println("onBeforeCompile vertex:\n${shader.vertexShader}")
//            println("onBeforeCompile fragment:\n${shader.fragmentShader}")
//        }
    }

    private val pixelsMesh = Mesh(planeGeometry, material)

    override fun addTo(scene: VizScene) {
        scene.add(VizObj(pixelsMesh))
    }

    override fun removeFrom(scene: VizScene) {
        scene.remove(VizObj(pixelsMesh))
    }

    fun getPixelLocationsInModelSpace(): Array<Vector3> = positions

    fun getPixelLocationsInPanelSpace(surfaceVisualizer: SurfaceVisualizer): Array<Vector2> {
        val panelGeom = surfaceVisualizer.geometry.clone()
        val pixGeom = pixGeometry.clone()

        val straightOnNormal = Vector3(0, 0, 1)

        // Rotate to straight on.
        val rotator = Rotator(surfaceVisualizer.panelNormal, straightOnNormal)
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

    companion object {
        private val roundLightTx = TextureLoader().load(
            "$resourcesBase/visualizer/textures/round.png",
            { println("loaded!") },
            { println("progress!") },
            { println("error!") }
        )
    }
}

class TexturePixelsVisualizer(
    positions: Array<Vector3>,
    normal: Vector3
) : BasePixelsVisualizer(positions, normal) {

    var texture: ThreeJsGlContext.ThreeTexture? = null
    var pixelOffset: Int = 0

    init {
        material.uniforms[""]
    }

    fun receivedFrame(resultViews: List<ResultView>) {
        val resultView = resultViews.only("ResultView") as DirectResultView
        val threeTexture = resultView.texture as ThreeJsGlContext.ThreeTexture
        texture = threeTexture
        pixelOffset = resultView.pixelOffset

        planeGeometry.setAttribute("pixelIndex", resultView.pixelIndexBufferAttr)

        material.uniforms["colorsTextureWidth"] = jsObject { value = resultView.width }
        material.uniforms["pixelColorsTexture"] = jsObject { value = threeTexture.renderTarget.texture }
        material.uniforms["pixelOffset"] = jsObject { value = pixelOffset }
        material.uniformsNeedUpdate = true
        material.map = texture?.renderTarget?.texture
    }
}

class ColorBufferPixelsVisualizer(
    positions: Array<Vector3>,
    normal: Vector3
) : BasePixelsVisualizer(positions, normal), Pixels {
    override val size = positions.size

    private val vertexColorBufferAttr = Float32BufferAttribute(Float32Array(size * 3 * 4), 3)
    private val colorsAsInts = IntArray(size) // store colors as an int array too for Pixels.get()

    init {
        vertexColorBufferAttr.usage = DynamicDrawUsage
        planeGeometry.setAttribute("color", vertexColorBufferAttr)
        material.vertexColors = true
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

    fun readColors(reader: ByteArrayReader) {
        val pixelCount = reader.readInt()
        val minPixCount = min(size, pixelCount)
        for (i in 0 until minPixCount) {
            this[i] = Color.parseWithoutAlpha(reader)
        }
    }
}

actual class DirectResultBuffer actual constructor(
    private val gl: GlContext,
    private val resultIndex: Int
) : IResultBuffer {
    private val texture = gl.runInContext { gl.createTexture() }
    private val pixelIndexBufferAttr = Float32BufferAttribute(Float32Array(1), 1)

    private var width = -1
    private var height = -1

    override fun resize(width: Int, height: Int, renderTargets: List<FixtureRenderTarget>) {
        gl.runInContext {
            gl.check { bindTexture(GL_TEXTURE_2D, texture.texture) }
            gl.check {
                texImage2D(
                    GL_TEXTURE_2D, 0, ColorResultType.readPixelFormat,
                    width, height, 0,
                    ColorResultType.readPixelFormat, ColorResultType.readType, nuffinBuffer()
                )
            }
            gl.check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); }
            gl.check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); }
            gl.check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE); }
            gl.check { bindTexture(GL_TEXTURE_2D, null) }
        }

        this.width = width
        this.height = height

        val length = width * height
        val array = Float32Array(length)
        for (i in 0 until length) {
            array[i * 3] = i.toFloat()
            array[i * 3 + 1] = i.toFloat()
            array[i * 3 + 2] = i.toFloat()
            array[i * 3 + 3] = i.toFloat()
        }
        pixelIndexBufferAttr.array = array.asDynamic()
        pixelIndexBufferAttr.count = length
        pixelIndexBufferAttr.usage = DynamicDrawUsage
        pixelIndexBufferAttr.needsUpdate = true
    }

    override fun attachTo(fb: GlContext.FrameBuffer) {
        fb.attach(texture.texture, GL_COLOR_ATTACHMENT0 + resultIndex)
    }

    override fun afterFrame(frameBuffer: GlContext.FrameBuffer) {
    }

    override fun getView(pixelOffset: Int, pixelCount: Int): DirectResultView {
        return DirectResultView(pixelIndexBufferAttr, texture, width, height, pixelOffset, pixelCount)
    }

    override fun release() {
        gl.runInContext { texture.release() }
    }
}

class DirectResultView(
    val pixelIndexBufferAttr: Float32BufferAttribute,
    val texture: GlContext.GlTexture,
    val width: Int,
    val height: Int,
    pixelOffset: Int,
    pixelCount: Int
) : ResultView(pixelOffset, pixelCount)

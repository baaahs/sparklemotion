package baaahs.gl.render

import baaahs.fixtures.Fixture
import baaahs.geom.Vector3F
import baaahs.gl.GlBase
import baaahs.gl.glsl.GlslProgram
import baaahs.model.Model
import com.danielgergely.kgl.*
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.window

interface ShaderPreview {
    fun start()
    fun stop()
    fun destroy()
    fun setProgram(program: GlslProgram)
    fun render()
    fun resize(width: Int, height: Int)
}

class ProjectionPreview(
    canvas2d: HTMLCanvasElement,
    gl: GlBase.JsGlContext,
    private var width: Int,
    private var height: Int,
    model: Model<*>,
    private val preRenderCallback: (() -> Unit)? = null
) : ShaderPreview {
    private var running = false
    private val modelRenderer = ModelRenderer(gl, model, FloatsResultFormat(gl.webgl))
    private var projectionProgram: GlslProgram? = null
    private val fixtureRenderPlans: Map<Model.Surface, FixtureRenderPlan>
    private val context2d = canvas2d.getContext("2d") as CanvasRenderingContext2D

    init {
        fixtureRenderPlans = model.allSurfaces.associateWith { surface ->
            modelRenderer.addFixture(object : Fixture {
                val lineVertices = surface.lines.flatMap { it.vertices }

                override val pixelCount: Int
                    get() = lineVertices.size
                override val pixelLocations: List<Vector3F?>?
                    get() = lineVertices

                override fun describe(): String = surface.name
            })
        }
    }

    override fun start() {
        running = true
        render()
    }

    override fun stop() {
        running = false
    }

    override fun destroy() {
        stop()
//        scene.release()
        projectionProgram?.release()
    }

    override fun setProgram(program: GlslProgram) {
        fixtureRenderPlans.forEach { (_, fixtureRenderPlan) ->
            fixtureRenderPlan.program = program
        }
        projectionProgram = program
    }

    override fun render() {
        if (!running) return

        if (projectionProgram != null) {
            preRenderCallback?.invoke()

            modelRenderer.draw()

            context2d.strokeStyle = "#ffffff"
            context2d.lineWidth = 2.0
            context2d.fillStyle = "black"
            context2d.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())
            context2d.beginPath()

            fixtureRenderPlans.forEach { (surface, fixtureRenderPlan) ->
                val projectedVertices = fixtureRenderPlan.renderResult as FloatsResult
                var vertexIndex = 0

                surface.lines.forEach { line ->
                    line.vertices.forEachIndexed { vIndex, _ ->
                        val pointX = projectedVertices.getR(vertexIndex).toDouble() * width
                        val pointY = (1 - projectedVertices.getG(vertexIndex).toDouble()) * height
                        if (vIndex == 0) {
                            context2d.moveTo(pointX, pointY)
                        } else {
                            context2d.lineTo(pointX, pointY)
                        }
                        vertexIndex++
                    }
                }
            }

            context2d.stroke()
        }

        window.requestAnimationFrame { render() }
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

//    inner class Scene {
//        private val vertexShader = gl.createVertexShader(GlslProgram.vertexShader)
//        private val fragmentShader = gl.createFragmentShader("""
//                    void main(void) { gl_FragColor = vec4(.5, 1., .5, 1.); }
//                """.trimIndent())
//        private val program = gl.compile(vertexShader, fragmentShader)
//
//        fun render() {
//            gl.runInContext {
//                gl.check { viewport(0, 0, width, height) }
//                gl.check { clearColor(1f, 0f, 0f, 1f) }
//                gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }
//
//                fixtureRenderPlans.forEach { (surface, fixtureRenderPlan) ->
//                    surface.lines.forEach { line ->
//                        gl.check { dra }
//                    }
//                    fixtureRenderPlan.pixels.
//                }
//            }
//        }
//
//        fun release() {
//            gl.runInContext {
//                gl.check { deleteShader(vertexShader) }
//                gl.check { deleteShader(fragmentShader) }
////                gl.check { deleteProgram(program) }
//            }
//        }
//    }

    companion object {
        private val quadRect = Quad.Rect(1f, -1f, -1f, 1f)
    }
}

class FloatsResultFormat(gl: WebGL2RenderingContext) : ModelRenderer.ResultFormat {
    init {
        gl.getExtension("EXT_color_buffer_float")!!
    }

    override val renderPixelFormat: Int
        get() = ModelRenderer.GlConst.GL_RGBA32F
    override val readPixelFormat: Int
        get() = GL_RGBA
    override val readType: Int
        get() = GL_FLOAT

    override fun createRenderResult(modelRenderer: ModelRenderer, size: Int, nextPixelOffset: Int): RenderResult {
        return FloatsResult(modelRenderer, size, nextPixelOffset)
    }

    override fun createBuffer(size: Int): Buffer {
        return FloatBuffer(size * 4)
    }

}

class FloatsResult(
    private val modelRenderer: ModelRenderer,
    override val size: Int,
    override val bufferOffset: Int
) : RenderResult {
    fun getR(i: Int): Float = getFloat(i, 0)
    fun getG(i: Int): Float = getFloat(i, 1)
    fun getB(i: Int): Float = getFloat(i, 2)
    fun getA(i: Int): Float = getFloat(i, 3)

    private fun getFloat(i: Int, component: Int): Float {
        val floatBuffer = modelRenderer.arrangement.resultBuffer as FloatBuffer
        val offset = (bufferOffset + i) * 4
        return floatBuffer[offset + component]
    }
}
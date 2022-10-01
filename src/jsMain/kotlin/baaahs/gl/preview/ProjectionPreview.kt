package baaahs.gl.preview

import baaahs.device.FixtureType
import baaahs.device.PixelFormat
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.fixtures.NullTransport
import baaahs.fixtures.PixelArrayFixture
import baaahs.fixtures.ProgramRenderPlan
import baaahs.geom.Vector2D
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.ModelRenderEngine
import baaahs.gl.render.pickResultDeliveryStrategy
import baaahs.gl.result.Vec2ResultType
import baaahs.model.Model
import baaahs.model.PixelArray
import baaahs.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D
import three.js.Vector2

class ProjectionPreview(
    canvas2d: HTMLCanvasElement,
    gl: GlContext,
    private var width: Int,
    private var height: Int,
    model: Model,
    private val preRenderCallback: (() -> Unit)? = null
) : ShaderPreview {
    private var running = false
    private val fixtureType = ProjectionPreviewDevice
    override val renderEngine = ModelRenderEngine(
        gl, fixtureType, resultDeliveryStrategy = gl.pickResultDeliveryStrategy()
    )
    private var projectionProgram: GlslProgram? = null
    private val renderTargets: Map<Model.Entity, FixtureRenderTarget>

    init {
        renderTargets = buildMap {
            model.visit { entity ->
                when (entity) {
                    is Model.Surface -> {
                        val lineVertices = entity.lines.flatMap { it.vertices }

                        // It's not really a PixelArrayFixture, we're just hijacking it to pass in surface perimeter vectors.
                        val fixture = object : PixelArrayFixture(
                            entity, lineVertices.size, entity.name, NullTransport,
                            PixelFormat.RGB8, 1f, lineVertices
                        ) {
                            override val fixtureType: FixtureType
                                get() = this@ProjectionPreview.fixtureType
                        }
                        put(entity, renderEngine.addFixture(fixture))
                    }
                    is PixelArray -> {
                        val pixelCount = entity.defaultFixtureConfig?.componentCount ?: 100
                        // It's not really a PixelArrayFixture, we're just hijacking it to pass in surface perimeter vectors.
                        val fixture = object : PixelArrayFixture(
                            entity, pixelCount, entity.name, NullTransport,
                            PixelFormat.RGB8, 1f, entity.calculatePixelLocalLocations(pixelCount)
                        ) {
                            override val fixtureType: FixtureType
                                get() = this@ProjectionPreview.fixtureType
                        }
                        put(entity, renderEngine.addFixture(fixture))
                    }
                }
            }
        }
    }

    private val context2d = canvas2d.getContext("2d") as CanvasRenderingContext2D

    override fun start() {
        running = true
        render()
    }

    override fun stop() {
        running = false
    }

    override fun destroy() {
        stop()
        projectionProgram?.release()
        renderEngine.release()
    }

    override fun setProgram(program: GlslProgram?) {
        renderEngine.setRenderPlan(
            FixtureTypeRenderPlan(
                listOf(ProgramRenderPlan(program, renderTargets.values.toList()))
            )
        )
        projectionProgram = program
    }

    override fun render() {
        if (!running) return

        GlobalScope.launch { asyncRender() }
    }

    private suspend fun asyncRender() {
        if (projectionProgram != null) {
            preRenderCallback?.invoke()

            renderEngine.draw()
            renderEngine.finish()

            context2d.strokeStyle = "#ffffff"
            context2d.lineWidth = 2.0
            context2d.fillStyle = "#000000"
            context2d.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())

            val errorMargin = 3.0
            val overflows = arrayListOf<Vector2>()

            renderTargets.forEach { (entity, renderTarget) ->
                val projectedVertices = renderTarget.fixtureResults as Vec2ResultType.Vec2FixtureResults

                fun getPoint(vertexIndex: Int, errorMargin: Double): Vector2D {
                    val insetWidth = width - errorMargin * 2
                    val insetHeight = height - errorMargin * 2

                    val vec2 = projectedVertices[renderTarget.component0Index + vertexIndex]
                    val u = vec2.x.toDouble()
                    val v = 1 - vec2.y.toDouble()

                    val point = Vector2D(
                        u * insetWidth + errorMargin,
                        v * insetHeight + errorMargin
                    )

                    var overflowX = point.x + (errorMargin - 1) / 2
                    var overflowY = point.y + (errorMargin - 1) / 2
                    var isOverflow = false
                    if (u < 0) {
                        overflowX = 0.0
                        isOverflow = true
                    } else if (u >= 1) {
                        overflowX = insetWidth + errorMargin
                        isOverflow = true
                    }
                    if (v < 0) {
                        overflowY = 0.0
                        isOverflow = true
                    } else if (v >= 1) {
                        overflowY = insetHeight + errorMargin
                        isOverflow = true
                    }
                    if (isOverflow) {
                        overflows.add(Vector2(overflowX, overflowY))
                    }
                    return point
                }

                var vertexIndex = 0

                val path = Path2D()
                when (entity) {
                    is Model.Surface -> {
                        entity.lines.forEach { line ->
                            line.vertexIndices.forEachIndexed { vIndex, _ ->
                                val point = getPoint(vertexIndex, errorMargin)
                                if (vIndex == 0) {
                                    path.moveTo(point.x, point.y)
                                } else {
                                    path.lineTo(point.x, point.y)
                                }

                                vertexIndex++
                            }
                        }
                    }
                    is PixelArray -> {
                        val fixture = renderTarget.fixture as PixelArrayFixture
                        (0 until fixture.componentCount).forEach { pIndex ->
                            val point = getPoint(vertexIndex, errorMargin)
                            context2d.fillStyle = "#ffffff"
                            context2d.fillRect(point.x, point.y, 3.0, 3.0)

                            vertexIndex++
                        }
                    }
                    else -> {
                        // TODO
                    }
                }
                path.closePath()
                context2d.stroke(path)
            }

            // Show overflow vertices.
            context2d.fillStyle = "#ff0000"
            overflows.forEach { coords ->
                context2d.fillRect(coords.x, coords.y, errorMargin, errorMargin)
            }
        }

        window.requestAnimationFrame { render() }
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
}
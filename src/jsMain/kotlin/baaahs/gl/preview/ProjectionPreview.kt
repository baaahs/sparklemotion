package baaahs.gl.preview

import baaahs.device.EnumeratedPixelLocations
import baaahs.device.PixelArrayDevice
import baaahs.device.PixelFormat
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.fixtures.NullTransport
import baaahs.fixtures.ProgramRenderPlan
import baaahs.geom.Vector2D
import baaahs.geom.Vector3F
import baaahs.get2DContext
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.ComponentRenderEngine
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.pickResultDeliveryStrategy
import baaahs.gl.result.Vec2ResultType
import baaahs.model.Model
import baaahs.model.PixelArray
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import three.Vector2
import web.animations.requestAnimationFrame
import web.canvas.Path2D
import web.html.HTMLCanvasElement

class ProjectionPreview(
    canvas2d: HTMLCanvasElement,
    gl: GlContext,
    private var width: Int,
    private var height: Int,
    model: Model,
    private val preRenderCallback: ((ShaderPreview) -> Unit)? = null
) : ShaderPreview {
    private var running = false
    private val fixtureType = ProjectionPreviewDevice
    override val renderEngine = ComponentRenderEngine(
        gl, fixtureType, resultDeliveryStrategy = gl.pickResultDeliveryStrategy()
    )
    override var program: GlslProgram? = null
        set(value) {
            field = value
            renderEngine.setRenderPlan(
                FixtureTypeRenderPlan(
                    listOf(ProgramRenderPlan(program, renderTargets.values.toList()))
                )
            )
        }
    private val renderTargets: Map<Model.Entity, FixtureRenderTarget> = buildMap {
        fun addFixture(entity: Model.Entity, pixelLocations: List<Vector3F>) {
            val pixelCount = pixelLocations.size
            val fixture = Fixture(
                entity, pixelCount, entity.name, NullTransport,
                this@ProjectionPreview.fixtureType,
                PixelArrayDevice.Config(
                    pixelCount, PixelFormat.RGB8, 1f,
                    EnumeratedPixelLocations(pixelLocations)
                )
            )
            put(entity, renderEngine.addFixture(fixture))
        }

        model.visit { entity ->
            when (entity) {
                is Model.Surface -> {
                    val lineVertices = entity.lines.flatMap { it.vertices }

                    // It's not really a PixelArray, we're just hijacking it to pass in surface perimeter vectors.
                    addFixture(entity, lineVertices)
                }
                is PixelArray -> {
                    val pixelCount = entity.defaultFixtureOptions?.componentCount ?: 100
                    val pixelLocations = entity.calculatePixelLocalLocations(pixelCount)
                    addFixture(entity, pixelLocations)
                }
            }
        }
    }

    private val context2d = canvas2d.get2DContext()

    override fun start() {
        running = true
        render()
    }

    override fun stop() {
        running = false
    }

    override fun destroy() {
        stop()
        renderEngine.release()
    }

    override fun render() {
        if (!running) return

        GlobalScope.launch { asyncRender() }
    }

    private suspend fun asyncRender() {
        if (program != null) {
            preRenderCallback?.invoke(this)

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
                        val fixture = renderTarget.fixture
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

        requestAnimationFrame { render() }
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
}
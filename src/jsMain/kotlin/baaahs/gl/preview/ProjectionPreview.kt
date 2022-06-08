package baaahs.gl.preview

import baaahs.device.FixtureType
import baaahs.device.PixelFormat
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.fixtures.NullTransport
import baaahs.fixtures.PixelArrayFixture
import baaahs.fixtures.ProgramRenderPlan
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.ModelRenderEngine
import baaahs.gl.result.Vec2ResultType
import baaahs.model.Model
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
    override val renderEngine = ModelRenderEngine(gl, fixtureType)
    private var projectionProgram: GlslProgram? = null
    private val renderTargets = model.allEntities
        .filterIsInstance<Model.Surface>() // TODO: Display all entity types, not just surfaces!
        .associateWith { surface ->
            val lineVertices = surface.lines.flatMap { it.vertices }

            // It's not really a PixelArrayFixture, we're just hijacking it to pass in surface perimeter vectors.
            val fixture = object : PixelArrayFixture(
                surface, lineVertices.size, surface.name, NullTransport,
                PixelFormat.RGB8, 1f, lineVertices
            ) {
                override val fixtureType: FixtureType
                    get() = this@ProjectionPreview.fixtureType
            }
            renderEngine.addFixture(fixture)
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
            val insetWidth = width - errorMargin * 2
            val insetHeight = height - errorMargin * 2

            val overflows = arrayListOf<Vector2>()

            renderTargets.forEach { (surface, renderTarget) ->
                val projectedVertices = renderTarget.fixtureResults as Vec2ResultType.Vec2FixtureResults
                var vertexIndex = 0

                val path = Path2D()
                surface.lines.forEach { line ->
                    line.vertexIndices.forEachIndexed { vIndex, _ ->
                        val vec2 = projectedVertices[renderTarget.component0Index + vertexIndex]
                        val u = vec2.x.toDouble()
                        val v = 1 - vec2.y.toDouble()

                        val pointX = u * insetWidth + errorMargin
                        val pointY = v * insetHeight + errorMargin
                        if (vIndex == 0) {
                            path.moveTo(pointX, pointY)
                        } else {
                            path.lineTo(pointX, pointY)
                        }

                        var overflowX = pointX + (errorMargin - 1) / 2
                        var overflowY = pointY + (errorMargin - 1) / 2
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

                        vertexIndex++
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
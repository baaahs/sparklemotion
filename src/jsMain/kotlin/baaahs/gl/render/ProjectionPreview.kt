package baaahs.gl.render

import baaahs.fixtures.DeviceTypeRenderPlan
import baaahs.fixtures.Fixture
import baaahs.fixtures.NullTransport
import baaahs.fixtures.ProgramRenderPlan
import baaahs.gl.GlBase
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.preview.ProjectionPreviewDevice
import baaahs.model.Model
import baaahs.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D

class ProjectionPreview(
    canvas2d: HTMLCanvasElement,
    gl: GlBase.JsGlContext,
    private var width: Int,
    private var height: Int,
    model: Model,
    private val preRenderCallback: (() -> Unit)? = null
) : ShaderPreview {
    private var running = false
    private val deviceType = ProjectionPreviewDevice
    override val renderEngine = ModelRenderEngine(gl, model, deviceType)
    private var projectionProgram: GlslProgram? = null
    private val renderTargets = model.allSurfaces.associateWith { surface ->
        val lineVertices = surface.lines.flatMap { it.vertices }
        val fixture = Fixture(surface, lineVertices.size, lineVertices, deviceType, transport = NullTransport)
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
    }

    override fun setProgram(program: GlslProgram) {
        renderEngine.setRenderPlan(
            DeviceTypeRenderPlan(
                listOf(ProgramRenderPlan(program, renderTargets.values.toList()))
            )
        )
        projectionProgram = program
    }

    override fun render() {
        if (!running) return

        if (projectionProgram != null) {
            preRenderCallback?.invoke()

            renderEngine.draw()

            context2d.strokeStyle = "#ffffff"
            context2d.lineWidth = 2.0
            context2d.fillStyle = "#000000"
            context2d.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())

            val errorMargin = 3.0
            val insetWidth = width - errorMargin * 2
            val insetHeight = height - errorMargin * 2

            val overflows = arrayListOf<DoubleArray>()

            renderTargets.forEach { (surface, renderTarget) ->
                val projectedVertices = deviceType.getVertexLocations(renderTarget.resultViews)
                var vertexIndex = 0

                val path = Path2D()
                surface.lines.forEach { line ->
                    line.vertices.forEachIndexed { vIndex, _ ->
                        val vec2 = projectedVertices[renderTarget.pixel0Index + vertexIndex]
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
                            overflows.add(doubleArrayOf(overflowX, overflowY))
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
                context2d.fillRect(coords[0], coords[1], errorMargin, errorMargin)
            }
        }

        window.requestAnimationFrame { render() }
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
}
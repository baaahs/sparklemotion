package baaahs.gl.preview

import baaahs.fixtures.*
import baaahs.gl.GlBase
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.ModelRenderEngine
import baaahs.model.Model
import baaahs.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

class MovingHeadPreview(
    canvas2d: HTMLCanvasElement,
    gl: GlBase.JsGlContext,
    private var width: Int,
    private var height: Int,
    model: Model,
    private val preRenderCallback: (() -> Unit)? = null
) : ShaderPreview {
    private var running = false
    private val deviceType = MovingHeadDevice
    override val renderEngine = ModelRenderEngine(gl, model, deviceType)
    private var movingHeadProgram: GlslProgram? = null
    private val renderTargets = model.movingHeads.associateWith { surface ->
        val fixture = Fixture(surface, 1, emptyList(), deviceType, transport = NullTransport)
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
        movingHeadProgram?.release()
    }

    override fun setProgram(program: GlslProgram?) {
        renderEngine.setRenderPlan(
            DeviceTypeRenderPlan(
                listOf(ProgramRenderPlan(program, renderTargets.values.toList()))
            )
        )
        movingHeadProgram = program
    }

    override fun render() {
        if (!running) return

        if (movingHeadProgram != null) {
            preRenderCallback?.invoke()

            renderEngine.draw()

            context2d.strokeStyle = "#ffffff"
            context2d.lineWidth = 2.0
            context2d.fillStyle = "#000000"
            context2d.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())

            context2d.fillStyle = "#ffffff"
            context2d.fillText("Fixture\tPan\tTilt\tColor\tDimmer", 3.0, 15.0)
            var y = 30.0

            renderTargets.forEach { (surface, renderTarget) ->
                val params = deviceType.getResults(renderTarget.resultViews).buffer.get(0)
                context2d.fillText("${surface.name}\t${params.pan}\t${params.tilt}\t${params.colorWheel}\t${params.dimmer}",
                    3.0, y)
                y += 15.0
            }
        }

        window.requestAnimationFrame { render() }
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
}
package baaahs.gl.preview

import baaahs.fixtures.*
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.ModelRenderEngine
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.plugin.core.MovingHeadParams
import baaahs.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.roundToInt

private val Float.short: String
    get() = try {
        ((this * 100).roundToInt() / 100.0).toString()
    } catch(e: Exception) {
        this.toString()
    }

class MovingHeadPreview(
    canvas2d: HTMLCanvasElement,
    gl: GlContext,
    private var width: Int,
    private var height: Int,
    model: Model,
    private val preRenderCallback: (() -> Unit)? = null
) : ShaderPreview {
    private var running = false
    private val deviceType = MovingHeadDevice
    override val renderEngine = ModelRenderEngine(gl, deviceType)
    private var movingHeadProgram: GlslProgram? = null
    private val renderTargets = model.allEntities
        .filterIsInstance<MovingHead>()
        .associateWith { movingHead ->
            val fixture = Fixture(movingHead, 1, emptyList(), deviceType.defaultConfig, transport = NullTransport)
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
        renderEngine.release()
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

        GlobalScope.launch { asyncRender() }
    }

    private suspend fun asyncRender() {
        if (movingHeadProgram != null) {
            preRenderCallback?.invoke()

            renderEngine.draw()
            renderEngine.finish()

            context2d.strokeStyle = "#ffffff"
            context2d.lineWidth = 2.0
            context2d.fillStyle = "#000000"
            context2d.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())

            context2d.fillStyle = "#ffffff"
            tabs("Fixture", "Pan", "Tilt", "Color Wheel", "Dimmer", 15.0)
            var y = 30.0

            renderTargets.forEach { (movingHead, renderTarget) ->
                val params = (renderTarget.fixtureResults as MovingHeadParams.ResultBuffer.FixtureResults)
                    .movingHeadParams

                tabs(
                    movingHead.name,
                    params.pan.short, params.tilt.short, params.colorWheel.short, params.dimmer.short,
                    y
                )
                y += 15.0
            }
        }

        window.requestAnimationFrame { render() }
    }

    private fun tabs(col0: String, col1: String, col2: String, col3: String, col4: String, y: Double) {
        context2d.fillText(col0, 3.0, y)
        context2d.fillText(col1, 50.0, y)
        context2d.fillText(col2, 100.0, y)
        context2d.fillText(col3, 150.0, y)
        context2d.fillText(col4, 200.0, y)
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
}
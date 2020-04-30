package baaahs.shows

import baaahs.Color
import baaahs.Model
import baaahs.Show
import baaahs.ShowContext
import baaahs.gadgets.ColorPicker
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import baaahs.shaders.SineWaveShader
import baaahs.shaders.SolidShader
import kotlin.math.roundToInt
import kotlin.random.Random

object ThumpShow : Show("Thump") {
    override fun createRenderer(model: Model<*>, showContext: ShowContext) = object : Renderer {
        private val beatSource = showContext.getBeatSource()
        val colorPicker = showContext.getGadget("color", ColorPicker("Color"))

        val solidShader = SolidShader()
        val sineWaveShader = SineWaveShader()
        val compositorShader = CompositorShader(solidShader, sineWaveShader)

        private val shaderBufs = showContext.allSurfaces.map { surface ->
            val solidShaderBuffer = showContext.getShaderBuffer(surface, solidShader)

            val sineWaveShaderBuffer = showContext.getShaderBuffer(surface, sineWaveShader).apply {
                density = Random.nextFloat() * 20
            }

            val compositorShaderBuffer =
                showContext.getCompositorBuffer(surface, solidShaderBuffer, sineWaveShaderBuffer, CompositingMode.ADD, 1f)

            ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer)
        }

        private val movingHeadBuffers = model.movingHeads.map { showContext.getMovingHeadBuffer(it) }

        init {
//        println("Created new CompositeShow, we have ${shaderBufs.size} buffers")
        }

        override fun nextFrame() {
//            val theta = ((getTimeMillis() / 1000f) % (2 * PI)).toFloat()
            val beat = showContext.currentBeat

            var i = 0
            val beatColor: Color = Color.WHITE.fade(colorPicker.color, beat % 1)

            shaderBufs.forEach { shaderBuffer ->
                shaderBuffer.solidShaderBuffer.color = beatColor
                shaderBuffer.sineWaveShaderBuffer.color = beatColor
//                shaderBuffer.sineWaveShaderBuffer.theta = theta + i++
                shaderBuffer.compositorShaderBuffer.mode = CompositingMode.ADD
                shaderBuffer.compositorShaderBuffer.fade = 1f
            }

            movingHeadBuffers.forEach { buf ->
                buf.color = beatColor
                buf.pan = beat.roundToInt().toFloat() / 2
                buf.tilt = beat.roundToInt().toFloat() / 2
            }
        }
    }

    class ShaderBufs(
        val solidShaderBuffer: SolidShader.Buffer,
        val sineWaveShaderBuffer: SineWaveShader.Buffer,
        val compositorShaderBuffer: CompositorShader.Buffer
    )
}

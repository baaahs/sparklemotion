package baaahs.shows

import baaahs.Color
import baaahs.Model
import baaahs.Show
import baaahs.ShowRunner
import baaahs.gadgets.ColorPicker
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import baaahs.shaders.SineWaveShader
import baaahs.shaders.SolidShader
import kotlin.math.roundToInt
import kotlin.random.Random

object ThumpShow : Show("Thump") {
    override fun createRenderer(model: Model<*>, showRunner: ShowRunner) = object : Renderer {
        private val beatSource = showRunner.getBeatSource()
        val colorPicker = showRunner.getGadget("color", ColorPicker("Color"))

        val solidShader = SolidShader()
        val sineWaveShader = SineWaveShader()
        val compositorShader = CompositorShader(solidShader, sineWaveShader)

        private val shaderBufs = showRunner.allSurfaces.map { surface ->
            val solidShaderBuffer = showRunner.getShaderBuffer(surface, solidShader)

            val sineWaveShaderBuffer = showRunner.getShaderBuffer(surface, sineWaveShader).apply {
                density = Random.nextFloat() * 20
            }

            val compositorShaderBuffer =
                showRunner.getCompositorBuffer(surface, solidShaderBuffer, sineWaveShaderBuffer, CompositingMode.ADD, 1f)

            ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer)
        }

        private val movingHeadBuffers = model.movingHeads.map { showRunner.getMovingHeadBuffer(it) }

        init {
//        println("Created new CompositeShow, we have ${shaderBufs.size} buffers")
        }

        override fun nextFrame() {
//            val theta = ((getTimeMillis() / 1000f) % (2 * PI)).toFloat()
            val beat = showRunner.currentBeat

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

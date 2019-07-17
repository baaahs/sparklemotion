package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import baaahs.shaders.SineWaveShader
import baaahs.shaders.SolidShader
import kotlin.math.PI
import kotlin.random.Random

object ThumpShow : Show("Thump") {
    override fun createRenderer(sheepModel: SheepModel, showRunner: ShowRunner) = object : Renderer {
        private val beatProvider = showRunner.getBeatProvider()
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

        private val movingHeadBuffers = sheepModel.eyes.map { showRunner.getMovingHeadBuffer(it) }

        init {
//        println("Created new CompositeShow, we have ${shaderBufs.size} buffers")
        }

        override fun nextFrame() {
            val theta = ((getTimeMillis() / 1000f) % (2 * PI)).toFloat()
            val beat = beatProvider.beat

            var i = 0
            shaderBufs.forEach { shaderBuffer ->
                shaderBuffer.solidShaderBuffer.color = Color.BLACK.fade(colorPicker.color, beat % 1f)
                shaderBuffer.sineWaveShaderBuffer.color = if (beat < .2) Color.WHITE else Color.ORANGE
                shaderBuffer.sineWaveShaderBuffer.theta = theta + i++
                shaderBuffer.compositorShaderBuffer.mode = CompositingMode.ADD
                shaderBuffer.compositorShaderBuffer.fade = 1f
            }

            movingHeadBuffers.forEach { buf ->
                buf.color = colorPicker.color
                buf.pan = PI.toFloat() / 2
                buf.tilt = beat / PI.toFloat()
            }
        }
    }

    class ShaderBufs(
        val solidShaderBuffer: SolidShader.Buffer,
        val sineWaveShaderBuffer: SineWaveShader.Buffer,
        val compositorShaderBuffer: CompositorShader.Buffer
    )
}

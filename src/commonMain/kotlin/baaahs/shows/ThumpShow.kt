package baaahs.shows

import baaahs.Color
import baaahs.Model
import baaahs.Show
import baaahs.ShowApi
import baaahs.gadgets.ColorPicker
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import baaahs.shaders.SineWaveShader
import baaahs.shaders.SolidShader
import kotlin.math.roundToInt
import kotlin.random.Random

object ThumpShow : Show("Thump") {
    override fun createRenderer(model: Model<*>, showApi: ShowApi) = object : Renderer {
        private val beatSource = showApi.getBeatSource()
        val colorPicker = showApi.getGadget("color", ColorPicker("Color"))

        val solidShader = SolidShader()
        val sineWaveShader = SineWaveShader()
        val compositorShader = CompositorShader(solidShader, sineWaveShader)

        private val shaderBufs = showApi.allSurfaces.map { surface ->
            val solidShaderBuffer = showApi.getShaderBuffer(surface, solidShader)

            val sineWaveShaderBuffer = showApi.getShaderBuffer(surface, sineWaveShader).apply {
                density = Random.nextFloat() * 20
            }

            val compositorShaderBuffer =
                showApi.getCompositorBuffer(surface, solidShaderBuffer, sineWaveShaderBuffer, CompositingMode.ADD, 1f)

            ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer)
        }

        private val movingHeadBuffers = model.movingHeads.map { showApi.getMovingHeadBuffer(it) }

        init {
//        println("Created new CompositeShow, we have ${shaderBufs.size} buffers")
        }

        override fun nextFrame() {
//            val theta = ((getTimeMillis() / 1000f) % (2 * PI)).toFloat()
            val beat = showApi.currentBeat

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

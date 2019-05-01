package baaahs.shows

import baaahs.*
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShaderBuffer
import baaahs.shaders.SineWaveShaderBuffer
import baaahs.shaders.SolidShaderBuffer
import kotlin.math.PI
import kotlin.random.Random

val CompositeShow = object : ShowMeta("Composite") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner) = object : Show {
        private val colorPicker = showRunner.getColorPicker()

        private val shaderBufs = sheepModel.allPanels.map { panel ->
            val solidShader = showRunner.getSolidShader(panel)

            val sineWaveShader = showRunner.getSineWaveShader(panel).apply {
                buffer.density = Random.nextFloat() * 20
            }

            val compositorShader = showRunner.getCompositorShader(panel, solidShader, sineWaveShader)

            compositorShader.buffer.apply {
                mode = CompositingMode.ADD
                fade = 1f
            }

            ShaderBufs(solidShader.buffer, sineWaveShader.buffer, compositorShader.buffer)
        }

        private val movingHeadBuffers = sheepModel.eyes.map { showRunner.getMovingHead(it) }

        init {
//        println("Created new CompositeShow, we have ${shaderBufs.size} buffers")
        }

        override fun nextFrame() {
            val theta = ((getTimeMillis() / 1000f) % (2 * PI)).toFloat()

            var i = 0
            shaderBufs.forEach { shaderBuffer ->
                shaderBuffer.solidShaderBuffer.color = colorPicker.color
                shaderBuffer.sineWaveShaderBuffer.color = Color.WHITE
                shaderBuffer.sineWaveShaderBuffer.theta = theta + i++
                shaderBuffer.compositorShaderBuffer.mode = CompositingMode.ADD
                shaderBuffer.compositorShaderBuffer.fade = 1f
            }

            movingHeadBuffers.forEach { buf ->
                buf.colorWheel = buf.closestColorFor(colorPicker.color)
                buf.pan = PI.toFloat() / 2
                buf.tilt = theta / 2
            }
        }
    }

    inner class ShaderBufs(
        val solidShaderBuffer: SolidShaderBuffer,
        val sineWaveShaderBuffer: SineWaveShaderBuffer,
        val compositorShaderBuffer: CompositorShaderBuffer
    )
}

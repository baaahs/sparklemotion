package baaahs.shows

import baaahs.*
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShaderBuffer
import baaahs.shaders.SineWaveShaderBuffer
import baaahs.shaders.SolidShaderBuffer
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class CompositeShow(sheepModel: SheepModel, showRunner: ShowRunner) : Show {
    private val colorPicker = showRunner.getColorPicker()

    private val shaderBufs = sheepModel.allPanels.map { panel ->
        val solidShader = showRunner.getSolidShader(panel)

        val sineWaveShader = showRunner.getSineWaveShader(panel).apply {
            buffer.density = Random.nextFloat() * 10
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
        println("Created new CompositeShow, we have ${shaderBufs.size} buffers")
    }

    override fun nextFrame() {
        val seed = Random(0)
        val theta = ((getTimeMillis() / 1000f) % (2 * PI)).toFloat()

        shaderBufs.forEach { shaderBuffer ->
            shaderBuffer.solidShaderBuffer.color = colorPicker.color
            shaderBuffer.sineWaveShaderBuffer.color = Color.WHITE
            shaderBuffer.sineWaveShaderBuffer.theta = theta
            shaderBuffer.compositorShaderBuffer.mode = CompositingMode.ADD
            shaderBuffer.compositorShaderBuffer.fade = 1f
        }

        movingHeadBuffers.forEach { buf ->
            buf.colorWheel = buf.closestColorFor(colorPicker.color)
            buf.pan += (nextRandomFloat(seed) - .5).toFloat() / 5
            buf.tilt += (nextRandomFloat(seed) - .5).toFloat() / 5
        }
    }

    private fun nextRandomFloat(seed: Random) =
        sin(seed.nextDouble() + getTimeMillis() / 1000.toDouble())

    class Meta : ShowMeta("CompositeShow") {
        override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner) =
            CompositeShow(sheepModel, showRunner)
    }
}

private class ShaderBufs(
    val solidShaderBuffer: SolidShaderBuffer,
    val sineWaveShaderBuffer: SineWaveShaderBuffer,
    val compositorShaderBuffer: CompositorShaderBuffer
)
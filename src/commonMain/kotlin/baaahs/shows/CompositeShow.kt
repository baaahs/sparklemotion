package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import baaahs.shaders.SineWaveShader
import baaahs.shaders.SolidShader
import kotlin.math.PI
import kotlin.random.Random

object CompositeShow : Show.MetaData("Composite") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner) = object : Show {
        val colorPicker = showRunner.getGadget(ColorPicker("Color"))

        val solidShader = SolidShader()
        val sineWaveShader = SineWaveShader()

        private val shaderBufs = showRunner.allSurfaces.map { surface ->
            val solidShaderBuffer = showRunner.getShaderBuffer(surface, solidShader)
            val sineWaveShaderBuffer = showRunner.getShaderBuffer(surface, sineWaveShader).apply {
                density = Random.nextFloat() * 20
            }

            val compositorShaderBuffer =
                showRunner.getCompositorBuffer(surface, solidShaderBuffer, sineWaveShaderBuffer, CompositingMode.ADD)

            ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer)
        }

        private val movingHeadBuffers = sheepModel.eyes.map { showRunner.getMovingHead(it) }

        override fun nextFrame() {
            val theta = ((getTimeMillis() % 10000 / 1000f) % (2 * PI)).toFloat()

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

    class ShaderBufs(
        val solidShaderBuffer: SolidShader.Buffer,
        val sineWaveShaderBuffer: SineWaveShader.Buffer,
        val compositorShaderBuffer: CompositorShader.Buffer
    )
}

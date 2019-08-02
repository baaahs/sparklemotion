package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import baaahs.shaders.SineWaveShader
import baaahs.shaders.SolidShader
import kotlin.math.PI
import kotlin.random.Random

object CompositeShow : Show("Composite") {
    override fun createRenderer(sheepModel: SheepModel, showRunner: ShowRunner) = object : Renderer {
        val colorPicker = showRunner.getGadget("color", ColorPicker("Color"))

        val solidShader = SolidShader()
        val sineWaveShader = SineWaveShader()

        private val shaderBufs = showRunner.allSurfaces.associateWith { surface -> shaderBufsFor(surface) }
            .toMutableMap()

        private fun shaderBufsFor(surface: Surface): ShaderBufs {
            val solidShaderBuffer = showRunner.getShaderBuffer(surface, solidShader)
            val sineWaveShaderBuffer = showRunner.getShaderBuffer(surface, sineWaveShader).apply {
                density = Random.nextFloat() * 20
            }

            val compositorShaderBuffer =
                showRunner.getCompositorBuffer(surface, solidShaderBuffer, sineWaveShaderBuffer, CompositingMode.ADD)

            return ShaderBufs(solidShaderBuffer, sineWaveShaderBuffer, compositorShaderBuffer)
        }

        private val movingHeadBuffers = sheepModel.eyes.map { showRunner.getMovingHeadBuffer(it) }

        override fun nextFrame() {
            val theta = ((getTimeMillis() % 10000 / 1000f) % (2 * PI)).toFloat()

            var i = 0
            shaderBufs.values.forEach { shaderBuffer ->
                shaderBuffer.solidShaderBuffer.color = colorPicker.color
                shaderBuffer.sineWaveShaderBuffer.color = Color.WHITE
                shaderBuffer.sineWaveShaderBuffer.theta = theta + i++
                shaderBuffer.compositorShaderBuffer.mode = CompositingMode.ADD
                shaderBuffer.compositorShaderBuffer.fade = 1f
            }

            movingHeadBuffers.forEach { buf ->
                buf.color = colorPicker.color
                buf.pan = PI.toFloat() / 2
                buf.tilt = theta / 2
            }
        }

        override fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>) {
            removedSurfaces.forEach { shaderBufs.remove(it) }
            newSurfaces.forEach { shaderBufs[it] = shaderBufsFor(it) }
        }
    }

    class ShaderBufs(
        val solidShaderBuffer: SolidShader.Buffer,
        val sineWaveShaderBuffer: SineWaveShader.Buffer,
        val compositorShaderBuffer: CompositorShader.Buffer
    )
}

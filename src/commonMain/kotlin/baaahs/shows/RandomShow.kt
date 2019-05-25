package baaahs.shows

import baaahs.Color
import baaahs.SheepModel
import baaahs.Show
import baaahs.ShowRunner
import baaahs.dmx.Shenzarpy
import baaahs.shaders.PixelShader
import kotlin.random.Random

object RandomShow : Show("Random") {
    override fun createRenderer(sheepModel: SheepModel, showRunner: ShowRunner) = object : Renderer {
        val pixelShaderBuffers = showRunner.allSurfaces.map { surface ->
            showRunner.getShaderBuffer(surface, PixelShader())
        }
        val movingHeadBuffers = sheepModel.eyes.map { showRunner.getMovingHeadBuffer(it) }

        override fun nextFrame() {
            pixelShaderBuffers.forEach { shaderBuffer ->
                for (i in shaderBuffer.colors.indices) {
                    shaderBuffer.colors[i] = Color.random()
                }
            }

            movingHeadBuffers.forEach { shenzarpy ->
                shenzarpy.color = Color.random()
                shenzarpy.pan = Random.nextFloat() * Shenzarpy.panRange.endInclusive
                shenzarpy.tilt = Random.nextFloat() * Shenzarpy.tiltRange.endInclusive
            }
        }
    }
}

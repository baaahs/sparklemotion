package baaahs.shows

import baaahs.*
import baaahs.shaders.PixelShader
import kotlin.random.Random

object RandomShow : Show.MetaData("Random") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner) = object : Show {
        val pixelShaderBuffers = showRunner.allSurfaces.map { surface ->
            showRunner.getShaderBuffer(surface, PixelShader())
        }
        val movingHeadBuffers = sheepModel.eyes.map { showRunner.getMovingHead(it) }

        override fun nextFrame() {
            pixelShaderBuffers.forEach { shaderBuffer ->
                shaderBuffer.colors.forEachIndexed { i, pixel ->
                    shaderBuffer.colors[i] = Color.random()
                }
            }

            movingHeadBuffers.forEach { shenzarpy ->
                shenzarpy.colorWheel = shenzarpy.closestColorFor(Color.random())
                shenzarpy.pan = Random.nextFloat() * Shenzarpy.panRange.endInclusive
                shenzarpy.tilt = Random.nextFloat() * Shenzarpy.tiltRange.endInclusive
            }
        }
    }
}

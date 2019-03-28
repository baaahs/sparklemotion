package baaahs.shows

import baaahs.*
import kotlin.random.Random

class RandomShow(sheepModel: SheepModel, showRunner: ShowRunner) : Show {
    val pixelShaderBuffers = sheepModel.allPanels.map { showRunner.getPixelShaderBuffer(it) }
    val movingHeadBuffers = sheepModel.eyes.map { showRunner.getMovingHeadBuffer(it) }

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

    class Meta: ShowMeta("RandomShow") {
        override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner) =
            RandomShow(sheepModel, showRunner)
    }
}
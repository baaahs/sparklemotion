package baaahs.shows

import baaahs.*
import baaahs.shaders.RandomShader
import kotlin.random.Random

object RandomShow : Show("Random") {
    override fun createRenderer(sheepModel: SheepModel, showRunner: ShowRunner) = object : Renderer {
        init {
            showRunner.allSurfaces.map { surface -> showRunner.getShaderBuffer(surface, RandomShader()) }
        }

        val movingHeadBuffers = sheepModel.eyes.map { showRunner.getMovingHead(it) }

        override fun nextFrame() {
            movingHeadBuffers.forEach { shenzarpy ->
                shenzarpy.colorWheel = shenzarpy.closestColorFor(Color.random())
                shenzarpy.pan = Random.nextFloat() * Shenzarpy.panRange.endInclusive
                shenzarpy.tilt = Random.nextFloat() * Shenzarpy.tiltRange.endInclusive
            }
        }
    }
}

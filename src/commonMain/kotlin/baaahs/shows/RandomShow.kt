package baaahs.shows

import baaahs.Color
import baaahs.Model
import baaahs.Show
import baaahs.ShowRunner
import baaahs.dmx.Shenzarpy
import baaahs.shaders.RandomShader
import kotlin.random.Random

object RandomShow : Show("Random") {
    override fun createRenderer(model: Model<*>, showRunner: ShowRunner) = object : Renderer {
        init {
            showRunner.allSurfaces.map { surface -> showRunner.getShaderBuffer(surface, RandomShader()) }
        }

        val movingHeadBuffers = model.movingHeads.map { showRunner.getMovingHeadBuffer(it) }

        override fun nextFrame() {
            movingHeadBuffers.forEach { shenzarpy ->
                shenzarpy.color = Color.random()
                shenzarpy.pan = Random.nextFloat() * Shenzarpy.panRange.endInclusive
                shenzarpy.tilt = Random.nextFloat() * Shenzarpy.tiltRange.endInclusive
            }
        }
    }
}

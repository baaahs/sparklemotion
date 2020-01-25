package baaahs.shows

import baaahs.*
import baaahs.dmx.Shenzarpy
import baaahs.shaders.RandomShader
import kotlin.random.Random

object RandomShow : Show("Random") {
    override fun createRenderer(model: Model<*>, showApi: ShowApi) = object : Renderer {
        init {
            showApi.allSurfaces.map { surface -> showApi.getShaderBuffer(surface, RandomShader()) }
        }

        val movingHeadBuffers = model.movingHeads.map { showApi.getMovingHeadBuffer(it) }

        override fun nextFrame() {
            movingHeadBuffers.forEach { shenzarpy ->
                shenzarpy.color = Color.random()
                shenzarpy.pan = Random.nextFloat() * Shenzarpy.panRange.endInclusive
                shenzarpy.tilt = Random.nextFloat() * Shenzarpy.tiltRange.endInclusive
            }
        }
    }
}

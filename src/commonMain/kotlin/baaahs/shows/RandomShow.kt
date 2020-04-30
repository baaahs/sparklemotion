package baaahs.shows

import baaahs.Color
import baaahs.Model
import baaahs.Show
import baaahs.ShowContext
import baaahs.dmx.Shenzarpy
import baaahs.shaders.RandomShader
import kotlin.random.Random

object RandomShow : Show("Random") {
    override fun createRenderer(model: Model<*>, showContext: ShowContext) = object : Renderer {
        init {
            showContext.allSurfaces.map { surface -> showContext.getShaderBuffer(surface, RandomShader()) }
        }

        val movingHeadBuffers = model.movingHeads.map { showContext.getMovingHeadBuffer(it) }

        override fun nextFrame() {
            movingHeadBuffers.forEach { shenzarpy ->
                shenzarpy.color = Color.random()
                shenzarpy.pan = Random.nextFloat() * Shenzarpy.panRange.endInclusive
                shenzarpy.tilt = Random.nextFloat() * Shenzarpy.tiltRange.endInclusive
            }
        }
    }
}

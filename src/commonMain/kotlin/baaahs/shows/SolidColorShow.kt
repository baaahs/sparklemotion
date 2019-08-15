package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.shaders.SolidShader

object SolidColorShow : Show("Solid Color") {
    override fun createRenderer(model: Model<*>, showRunner: ShowRunner): Renderer {
        val colorPicker = showRunner.getGadget("color", ColorPicker("Color"))

        val shader = SolidShader()
        val shaderBuffers = showRunner.allSurfaces.map {
            showRunner.getShaderBuffer(it, shader).apply { color = Color.WHITE }
        }

        val eyes = model.movingHeads.map { eye -> showRunner.getMovingHeadBuffer(eye) }

        return object : Renderer {
            override fun nextFrame() {
                val color = colorPicker.color
                shaderBuffers.forEach { it.color = color }
                eyes.forEach { it.color = color }
            }
        }
    }
}
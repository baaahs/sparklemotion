package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.shaders.SolidShader

object SolidColorShow : Show("Solid Color") {
    override fun createRenderer(model: Model<*>, showApi: ShowApi): Renderer {
        val colorPicker = showApi.getGadget("color", ColorPicker("Color"))
        val saturationPicker = showApi.getGadget("sm_saturation", Slider("Saturation"))
        val brightnessPicker = showApi.getGadget("sm_brightness", Slider("Brightness"))

        val shader = SolidShader()
        val shaderBuffers = showApi.allSurfaces.map {
            showApi.getShaderBuffer(it, shader).apply { color = Color.ORANGE }
        }

        val eyes = model.movingHeads.map { eye -> showApi.getMovingHeadBuffer(eye) }

        return object : Renderer {
            override fun nextFrame() {
                val color = colorPicker.color
                shaderBuffers.forEach {
                    it.color = color.withSaturation(saturationPicker.value).withBrightness(brightnessPicker.value)
                }
                eyes.forEach { it.color = color }
            }
        }
    }
}
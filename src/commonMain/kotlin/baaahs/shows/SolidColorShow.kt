package baaahs.shows

import baaahs.Color
import baaahs.Model
import baaahs.Show
import baaahs.ShowContext
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.shaders.SolidShader

object SolidColorShow : Show("Solid Color") {
    override fun createRenderer(model: Model<*>, showContext: ShowContext): Renderer {
        val colorPicker = showContext.getGadget("color", ColorPicker("Color"))
        val saturationPicker = showContext.getGadget("sm_saturation", Slider("Saturation"))
        val brightnessPicker = showContext.getGadget("sm_brightness", Slider("Brightness"))

        val shader = SolidShader()
        val shaderBuffers = showContext.allSurfaces.map {
            showContext.getShaderBuffer(it, shader).apply { color = Color.ORANGE }
        }

        val eyes = model.movingHeads.map { eye -> showContext.getMovingHeadBuffer(eye) }

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
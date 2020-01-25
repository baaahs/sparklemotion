package baaahs.shows

import baaahs.Model
import baaahs.Show
import baaahs.ShowApi
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.shaders.SimpleSpatialShader

object SimpleSpatialShow : Show("Spatial") {
    override fun createRenderer(model: Model<*>, showApi: ShowApi): Renderer {
        val colorPicker = showApi.getGadget("color", ColorPicker("Color"))
        val centerXSlider = showApi.getGadget("centerX", Slider("center X", 0.5f, 0f, 1f, 0.01f))
        val centerYSlider = showApi.getGadget("centerY", Slider("center Y", 0.5f, 0f, 1f, 0.01f))
        val radiusSlider = showApi.getGadget("radius", Slider("radius", 0.25f, 0f, 1f, 0.01f))

        val shader = SimpleSpatialShader()
        val shaderBuffers = showApi.allSurfaces.map {
            showApi.getShaderBuffer(it, shader)
        }

        return object : Renderer {
            override fun nextFrame() {
                shaderBuffers.forEach {
                    it.color = colorPicker.color
                    it.centerX = centerXSlider.value
                    it.centerY = centerYSlider.value
                    it.radius = radiusSlider.value
                }
            }
        }
    }
}
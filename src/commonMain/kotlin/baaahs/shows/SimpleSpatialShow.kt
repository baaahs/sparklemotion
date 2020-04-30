package baaahs.shows

import baaahs.Model
import baaahs.Show
import baaahs.ShowContext
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.shaders.SimpleSpatialShader

object SimpleSpatialShow : Show("Spatial") {
    override fun createRenderer(model: Model<*>, showContext: ShowContext): Renderer {
        val colorPicker = showContext.getGadget("color", ColorPicker("Color"))
        val centerXSlider = showContext.getGadget("centerX", Slider("center X", 0.5f, 0f, 1f, 0.01f))
        val centerYSlider = showContext.getGadget("centerY", Slider("center Y", 0.5f, 0f, 1f, 0.01f))
        val radiusSlider = showContext.getGadget("radius", Slider("radius", 0.25f, 0f, 1f, 0.01f))

        val shader = SimpleSpatialShader()
        val shaderBuffers = showContext.allSurfaces.map {
            showContext.getShaderBuffer(it, shader)
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
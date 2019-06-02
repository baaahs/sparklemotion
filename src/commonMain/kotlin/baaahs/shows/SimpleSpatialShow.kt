package baaahs.shows

import baaahs.SheepModel
import baaahs.Show
import baaahs.ShowRunner
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.shaders.SimpleSpatialShader

object SimpleSpatialShow : Show.MetaData("Spatial") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show {
        val colorPicker = showRunner.getGadget("color", ColorPicker("Color"))
        val centerXSlider = showRunner.getGadget("centerX", Slider("center X", 0.5f))
        val centerYSlider = showRunner.getGadget("centerY", Slider("center Y", 0.5f))
        val radiusSlider = showRunner.getGadget("radius", Slider("radius", 0.25f))

        val shader = SimpleSpatialShader()
        val shaderBuffers = showRunner.allSurfaces.map {
            showRunner.getShaderBuffer(it, shader)
        }

        return object : Show {
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
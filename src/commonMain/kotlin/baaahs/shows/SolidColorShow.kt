package baaahs.shows

import baaahs.Color
import baaahs.SheepModel
import baaahs.Show
import baaahs.ShowRunner
import baaahs.gadgets.ColorPicker
import baaahs.shaders.SolidShader

object SolidColorShow : Show("Solid Color") {
    override fun createRenderer(sheepModel: SheepModel, showRunner: ShowRunner): Renderer {
        val colorPicker = showRunner.getGadget("color", ColorPicker("Color"))

        val shader = SolidShader()
        val shaderBuffers = showRunner.allSurfaces.map {
            showRunner.getShaderBuffer(it, shader).apply { color = Color.WHITE }
        }

        return object : Renderer {
            var priorColor = colorPicker.color

            override fun nextFrame() {
                val color = colorPicker.color
                if (color != priorColor) {
                    shaderBuffers.forEach { it.color = color }
                    priorColor = color
                }
            }
        }
    }
}
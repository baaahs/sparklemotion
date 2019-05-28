package baaahs.shows

import baaahs.Color
import baaahs.SheepModel
import baaahs.Show
import baaahs.ShowRunner
import baaahs.gadgets.ColorPicker
import baaahs.shaders.SolidShader

object SolidColorShow : Show.MetaData("Solid Color") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show {
        val colorPicker = showRunner.getGadget(ColorPicker("Color"))

        val shader = SolidShader()
        val shaderBuffers = sheepModel.allPanels.map {
            showRunner.getShaderBuffer(it, shader).apply { color = Color.WHITE }
        }

        return object : Show {
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
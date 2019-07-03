package baaahs.shows

import baaahs.Color
import baaahs.SheepModel
import baaahs.Show
import baaahs.ShowRunner
import baaahs.gadgets.ColorPicker
import baaahs.shaders.PixelShader

object CreepingPixelsShow : Show("Creeping Pixels") {
    override fun createRenderer(sheepModel: SheepModel, showRunner: ShowRunner): Renderer {
        val colorPicker = showRunner.getGadget("color", ColorPicker("Color"))

        val shader = PixelShader(PixelShader.Encoding.INDEXED_2)
        val shaderBuffers = showRunner.allSurfaces.map {
            showRunner.getShaderBuffer(it, shader).apply {
                palette[0] = Color.BLACK
            }
        }

        return object : Renderer {
            var i: Int = 0

            override fun nextFrame() {
                val color = colorPicker.color

                shaderBuffers.forEach {
                    it.palette[1] = color
                    it.setAll(0)
                    it[i % 60] = 1
                }
                i++
            }
        }
    }
}
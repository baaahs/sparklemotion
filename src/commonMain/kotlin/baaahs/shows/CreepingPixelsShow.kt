package baaahs.shows

import baaahs.Color
import baaahs.Model
import baaahs.Show
import baaahs.ShowContext
import baaahs.gadgets.ColorPicker
import baaahs.shaders.PixelShader

object CreepingPixelsShow : Show("Creeping Pixels") {
    override fun createRenderer(model: Model<*>, showContext: ShowContext): Renderer {
        val colorPicker = showContext.getGadget("color", ColorPicker("Color"))

        val shader = PixelShader(PixelShader.Encoding.INDEXED_2)
        val shaderBuffers = showContext.allSurfaces.map {
            showContext.getShaderBuffer(it, shader).apply {
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
                    it[i % it.colors.size] = 1
                }
                i++
            }
        }
    }
}
package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.shaders.PixelShader

object CreepingPixelsShow : Show("Creeping Pixels") {
    override fun createRenderer(model: Model<*>, showApi: ShowApi): Renderer {
        val colorPicker = showApi.getGadget("color", ColorPicker("Color"))

        val shader = PixelShader(PixelShader.Encoding.INDEXED_2)
        val shaderBuffers = showApi.allSurfaces.map {
            showApi.getShaderBuffer(it, shader).apply {
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
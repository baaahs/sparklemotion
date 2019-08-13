package baaahs.shows

import baaahs.Gadget
import baaahs.Model
import baaahs.Show
import baaahs.ShowRunner
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.shaders.GlslShader

abstract class GlslShow(name: String) : Show(name) {
    abstract val program: String

    override fun createRenderer(model: Model<*>, showRunner: ShowRunner): Renderer {
        val shader = GlslShader(program)

        val adjustableValuesToGadgets = shader.adjustableValues.associateWith { it.createGadget(showRunner) }
        val buffers = showRunner.allSurfaces.map { showRunner.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
                buffers.forEach { buffer ->
                    adjustableValuesToGadgets.forEach { (adjustableValue, gadget) ->
                        val value: Any = when (gadget) {
                            is Slider -> gadget.value
                            is ColorPicker -> gadget.color
                            else -> throw IllegalArgumentException("unsupported gadget $gadget")
                        }
                        buffer.update(adjustableValue, value)
                    }
                }
            }
        }
    }

    fun GlslShader.AdjustableValue.createGadget(showRunner: ShowRunner): Gadget {
        val config = config
        val name = config.getPrimitive("name").contentOrNull ?: varName

        val gadget = when (gadgetType) {
            "Slider" -> {
                Slider(
                    name,
                    initialValue = config.getPrimitive("initialValue").floatOrNull ?: 1f,
                    minValue = config.getPrimitive("minValue").floatOrNull,
                    maxValue = config.getPrimitive("maxValue").floatOrNull
                )
            }
            "ColorPicker" -> {
                ColorPicker(name)
            }
            else -> throw IllegalArgumentException("unknown gadget ${gadgetType}")
        }

        return showRunner.getGadget(name, gadget)
    }

}
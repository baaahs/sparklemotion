package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.shaders.GlslShader

abstract class GlslShow(name: String) : Show(name) {
    abstract val program: String

    override fun createRenderer(model: Model<*>, showRunner: ShowRunner): Renderer {
        val shader = GlslShader(program)

        val adjustableValuesToGadgets = shader.adjustableValues.associateWith { it.createGadget(showRunner) }
        val buffers = showRunner.allSurfaces.associateWithTo(hashMapOf()) { showRunner.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
                buffers.values.forEach { buffer ->
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

            override fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>) {
                removedSurfaces.forEach { buffers.remove(it) }
                newSurfaces.forEach { buffers[it] = showRunner.getShaderBuffer(it, shader) }
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
                    initialValue = config.getPrimitiveOrNull("initialValue")?.float ?: 1f,
                    minValue = config.getPrimitiveOrNull("minValue")?.float ?: 0f,
                    maxValue = config.getPrimitiveOrNull("maxValue")?.float ?: 1f
                )
            }
            "ColorPicker" -> {
                ColorPicker(name)
            }
            else -> throw IllegalArgumentException("unknown gadget ${gadgetType}")
        }

        return showRunner.getGadget("glsl_${varName}", gadget)
    }

}
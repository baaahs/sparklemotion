package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.shaders.GlslShader

abstract class GlslShow(name: String) : Show(name) {
    abstract val program: String

    override fun createRenderer(model: Model<*>, showRunner: ShowRunner): Renderer {
        val shader = GlslShader(program, model.defaultUvTranslator)

        val paramDataSources = shader.params.map { it.createDataSource(showRunner) }
        val buffers = showRunner.allSurfaces.associateWithTo(hashMapOf()) { showRunner.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
                buffers.values.forEach { buffer ->
                    val bufferValues = paramDataSources.map { it.getValue() }
                    buffer.update(bufferValues)
                }
            }

            override fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>) {
                removedSurfaces.forEach { buffers.remove(it) }
                newSurfaces.forEach { buffers[it] = showRunner.getShaderBuffer(it, shader) }
            }
        }
    }

    fun GlslShader.Param.createDataSource(showRunner: ShowRunner): DataSource {
        val config = config
        val name = config.getPrimitive("name").contentOrNull ?: varName

        return when (gadgetType) {
            "Slider" -> {
                GadgetDataSource(showRunner.getGadget("glsl_${varName}", Slider(
                    name,
                    initialValue = config.getPrimitiveOrNull("initialValue")?.float ?: 1f,
                    minValue = config.getPrimitiveOrNull("minValue")?.float ?: 0f,
                    maxValue = config.getPrimitiveOrNull("maxValue")?.float ?: 1f
                )))
            }
            "ColorPicker" -> {
                GadgetDataSource(showRunner.getGadget("glsl_${varName}", ColorPicker(name)))
            }
            "Beat" -> {
                BeatDataSource(showRunner.getBeatSource().getBeatData(), showRunner.clock)
            }
            "StartOfMeasure" -> {
                StartOfMeasureDataSource(showRunner.getBeatSource().getBeatData(), showRunner.clock)
            }
            else -> throw IllegalArgumentException("unknown gadget ${gadgetType}")
        }
    }

    interface DataSource {
        fun getValue(): Any
    }

    class GadgetDataSource(val gadget: Gadget) : DataSource {
        override fun getValue(): Any {
            return when (gadget) {
                is Slider -> gadget.value
                is ColorPicker -> gadget.color
                else -> throw IllegalArgumentException("unsupported gadget $gadget")
            }
        }
    }

    class BeatDataSource(val beatData: BeatData, val clock: Clock) : DataSource {
        override fun getValue(): Any {
            return beatData.fractionTillNextBeat(clock)
        }
    }

    class StartOfMeasureDataSource(val beatData: BeatData, val clock: Clock) : DataSource {
        override fun getValue(): Any {
            return beatData.fractionTillNextMeasure(clock)
        }
    }
}
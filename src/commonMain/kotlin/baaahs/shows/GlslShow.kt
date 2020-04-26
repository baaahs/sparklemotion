package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.glshaders.GlslProgram
import baaahs.glsl.GlslRenderer
import baaahs.shaders.GlslShader

class GlslShow(name: String, val src: String, val isPreview: Boolean = false) : Show(name) {
    override fun createRenderer(model: Model<*>, showRunner: ShowRunner): Renderer {
        val patch = GlslProgram.autoWire(
            mapOf(
                "uv" to GlslRenderer.uvMapper,
                "color" to GlslRenderer.glslAnalyzer.asShader(src)
            )
        )

        val program = patch.compile()
        val shader = GlslShader(program, model.defaultUvTranslator)
        val userInputs = program.userInputs.map { binding ->
            val uniformInput = binding.uniformInput
            val name = uniformInput.name
            val varName = uniformInput.varName

            when (uniformInput.type) {
                "float" -> {
                    val slider = showRunner.getGadget(
                        "glsl_$varName",
                        Slider(
                            name //,
                            //                            initialValue = config.getPrimitiveOrNull("initialValue")?.float ?: 1f,
                            //                            minValue = config.getPrimitiveOrNull("minValue")?.float ?: 0f,
                            //                            maxValue = config.getPrimitiveOrNull("maxValue")?.float ?: 1f
                        )
                    )
                    object : GadgetDataSource(slider) {
                        override fun update() {
                            binding.uniformLocation?.set(slider.value)
                        }
                    }
                }
                "vec4" -> {
                    val colorPicker = showRunner.getGadget("glsl_$varName", ColorPicker(name))
                    object : GadgetDataSource(colorPicker) {
                        override fun update() {
                            binding.uniformLocation?.set(
                                colorPicker.color.redF,
                                colorPicker.color.greenF,
                                colorPicker.color.blueF,
                                colorPicker.color.alphaF
                            )
                        }
                    }
                }
//                "Beat" -> {
//                    BeatDataSource(showRunner.getBeatSource().getBeatData(), showRunner.clock)
//                }
//                "StartOfMeasure" -> {
//                    StartOfMeasureDataSource(showRunner.getBeatSource().getBeatData(), showRunner.clock, binding)
//                }
                else -> {
                    logger.info { "dunno how to handle uniform input type ${uniformInput.type}" }
                    null
                }
            } as DataSource
        }

        val buffers = showRunner.allSurfaces.associateWithTo(hashMapOf()) { showRunner.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
                program.gl.runInContext {
                    program.gl.check { useProgram(program.id) }
                    userInputs.forEach { userInput ->
                        userInput.update()
                    }
                }
//                buffers.values.forEach { buffer ->
//                    val bufferValues = paramDataSources.map { it.getValue() }
//                    buffer.update(bufferValues)
//                }
            }

            override fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>) {
                removedSurfaces.forEach { buffers.remove(it) }
                newSurfaces.forEach { buffers[it] = showRunner.getShaderBuffer(it, shader) }
            }
        }
    }

    interface DataSource {
        fun getValue(): Any
        fun update()
    }

    abstract class GadgetDataSource(val gadget: Gadget) : DataSource {
        override fun getValue(): Any {
            return when (gadget) {
                is Slider -> gadget.value
                is ColorPicker -> gadget.color
                else -> throw IllegalArgumentException("unsupported gadget $gadget")
            }
        }
    }

    abstract class BeatDataSource(val beatData: BeatData, val clock: Clock) : DataSource {
        override fun getValue(): Any {
            return beatData.fractionTillNextBeat(clock)
        }
    }

    abstract class StartOfMeasureDataSource(val beatData: BeatData, val clock: Clock, binding: GlslProgram.Binding) : DataSource {
        override fun getValue(): Any {
            return beatData.fractionTillNextMeasure(clock)
        }
    }

    companion object {
        val logger = Logger("GlslShow")
    }
}

package baaahs.shows

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.Patch
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslRenderer
import baaahs.glsl.Uniform
import baaahs.shaders.GlslShader

class GlslShow(
    name: String,
    val src: String,
    val glslContext: GlslContext,
    val isPreview: Boolean = false
) : Show(name) {
    override fun createRenderer(model: Model<*>, showContext: ShowContext): Renderer {
        val patch = AutoWirer().autoWire(
            mapOf(
                "uv" to GlslRenderer.uvMapper,
                "color" to GlslRenderer.glslAnalyzer.asShader(src)
            )
        )

        val program = patch.compile(glslContext)

        program.bind { uniformPort ->
            providerFromPlugin(uniformPort, program)
                ?: providerFromGadget(uniformPort, showContext)
        }

        val shader = GlslShader(program, model.defaultUvTranslator, glslContext)

        val buffers = showContext.allSurfaces.associateWithTo(hashMapOf()) { showContext.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
                program.prepareToDraw()

//                buffers.values.forEach { buffer ->
//                    val bufferValues = paramDataSources.map { it.getValue() }
//                    buffer.update(bufferValues)
//                }
            }

            override fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>) {
                removedSurfaces.forEach { buffers.remove(it) }
                newSurfaces.forEach { buffers[it] = showContext.getShaderBuffer(it, shader) }
            }
        }
    }

    private fun providerFromPlugin(
        uniformPort: Patch.UniformPort,
        program: GlslProgram
    ): GlslProgram.UniformProvider? {
        return (uniformPort as? GlslProgram.StockUniformPort)?.let {
            val plugins = Plugins.findAll()
            plugins.matchUniformProvider(uniformPort.type, uniformPort.pluginId, program)
        }
    }

    private fun providerFromGadget(
        uniformPort: Patch.UniformPort,
        showContext: ShowContext
    ): GlslProgram.UniformProvider? {
        val name = uniformPort.name
        val varName = uniformPort.varName

        when (uniformPort.type) {
            "float" -> {
                val slider = showContext.getGadget(
                    "glsl_$varName",
                    Slider(
                        name //,
                        //                            initialValue = config.getPrimitiveOrNull("initialValue")?.float ?: 1f,
                        //                            minValue = config.getPrimitiveOrNull("minValue")?.float ?: 0f,
                        //                            maxValue = config.getPrimitiveOrNull("maxValue")?.float ?: 1f
                    )
                )
                return object : GlslProgram.UniformProvider {
                    override fun set(uniform: Uniform) = uniform.set(slider.value)
                }
            }
            "vec4" -> {
                val colorPicker = showContext.getGadget("glsl_$varName", ColorPicker(name))
                return object : GlslProgram.UniformProvider {
                    override fun set(uniform: Uniform) {
                        uniform.set(
                            colorPicker.color.redF,
                            colorPicker.color.greenF,
                            colorPicker.color.blueF,
                            colorPicker.color.alphaF
                        )
                    }
                }
            }
            //                "Beat" -> {
            //                    BeatDataSource(showContext.getBeatSource().getBeatData(), showContext.clock)
            //                }
            //                "StartOfMeasure" -> {
            //                    StartOfMeasureDataSource(showContext.getBeatSource().getBeatData(), showContext.clock, binding)
            //                }
            else -> {
                logger.info { "dunno how to handle uniform input type ${uniformPort.type}" }
                return null
            }
        }
    }

//    abstract class BeatDataSource(val beatData: BeatData, val clock: Clock) : DataSource {
//        override fun getValue(): Any {
//            return beatData.fractionTillNextBeat(clock)
//        }
//    }
//
//    abstract class StartOfMeasureDataSource(val beatData: BeatData, val clock: Clock, binding: GlslProgram.Binding) : DataSource {
//        override fun getValue(): Any {
//            return beatData.fractionTillNextMeasure(clock)
//        }
//    }

    companion object {
        val logger = Logger("GlslShow")
    }
}

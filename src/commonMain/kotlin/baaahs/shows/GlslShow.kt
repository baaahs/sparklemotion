package baaahs.shows

import baaahs.*
import baaahs.glsl.GlslBase
import baaahs.glsl.GlslPlugin
import baaahs.glsl.Program
import baaahs.shaders.GlslShader

abstract class GlslShow(name: String) : Show(name) {
    abstract val program: Program

    override fun createRenderer(model: Model<*>, showApi: ShowApi): Renderer {
        val shader = GlslShader(program, model.defaultUvTranslator)

        val paramDataSources = program.params.map { it.createDataSource(showApi) }
        val buffers = showApi.allSurfaces.associateWithTo(hashMapOf()) { showApi.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
                buffers.values.forEach { buffer ->
                    val bufferValues = paramDataSources.map { it.getValue() }
                    buffer.update(bufferValues)
                }
            }

            override fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>) {
                removedSurfaces.forEach { buffers.remove(it) }
                newSurfaces.forEach { buffers[it] = showApi.getShaderBuffer(it, shader) }
            }
        }
    }

    private fun GlslShader.Param.createDataSource(showApi: ShowApi): DataSource {
        val config = config
        val name = config.getPrimitive("name").contentOrNull ?: varName

        return when (dataSourceProvider) {
            is GlslPlugin.GadgetDataSourceProvider -> {
                when (dataSourceProvider.name) {
                    // TODO: kill these:
                    "Beat" -> {
                        BeatDataSource(showApi.getBeatSource().getBeatData(), showApi.clock)
                    }
                    "StartOfMeasure" -> {
                        StartOfMeasureDataSource(showApi.getBeatSource().getBeatData(), showApi.clock)
                    }
                    else -> {
                        val gadgetPlugin = (Plugins.gadgets[dataSourceProvider.name]
                            ?: throw IllegalArgumentException("unknown gadget ${dataSourceProvider.name}"))
                                as GadgetPlugin<Gadget>

                        GadgetDataSource(
                            showApi.getGadget("glsl_${varName}", gadgetPlugin.create(name, config)),
                            gadgetPlugin
                        )
                    }
                }
            }
            is GlslPlugin.PluginDataSourceProvider -> {
                val plugin = GlslBase.plugins.find { it.name == dataSourceProvider.name }
                    ?: throw IllegalArgumentException("unknown plugin $dataSourceProvider")
                plugin.createDataSource(config)
            }
            else -> throw IllegalArgumentException("unknown source $dataSourceProvider")
        }
    }

    interface DataSource {
        fun getValue(): Any
    }

    class GadgetDataSource<T : Gadget>(val gadget: T, val gadgetPlugin: GadgetPlugin<T>) : DataSource {
        override fun getValue(): Any = gadgetPlugin.getValue(gadget)
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
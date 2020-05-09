package baaahs.shows

import baaahs.*
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.Patch
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslRenderer
import baaahs.shaders.GlslShader

open class GlslShow(
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

        val plugins = Plugins.findAll()
        val program = patch.compile(glslContext) { uniformPortRef: Patch.UniformPortRef ->
            plugins.matchUniformProvider(uniformPortRef, showContext, glslContext)
        }

        val shader = GlslShader(program, model.defaultUvTranslator, glslContext)

        val buffers = showContext.allSurfaces.associateWithTo(hashMapOf()) { showContext.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
                program.updateUniforms()

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

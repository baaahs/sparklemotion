package baaahs.device

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.Feed
import baaahs.gl.data.PerPixelEngineFeed
import baaahs.gl.data.PerPixelProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.param.FloatsParamBuffer
import baaahs.gl.param.ParamBuffer
import baaahs.gl.patch.ContentType
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.RenderTarget
import baaahs.gl.shader.InputPort
import baaahs.glsl.Uniform
import baaahs.plugin.SerializerRegistrar
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.util.Logger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:PixelIndex")
data class PixelIndexDataSource(@Transient val `_`: Boolean = true) : DataSource {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Pixel Index"
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType
        get() = ContentType.XyzCoordinate

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        return PixelIndexFeed(getVarName(id), "ds_${id}_texture")
    }

    override fun appendDeclaration(buf: StringBuilder, id: String) {
        val textureUniformId = "ds_${id}_texture"
        val varName = getVarName(id)
        buf.append("""
            uniform sampler2D $textureUniformId;
            int ds_${id}_getPixelIndex(vec2 rasterCoord) {
                return int(texelFetch($textureUniformId, ivec2(rasterCoord.xy), 0).x);
            }
            int $varName;
            
        """.trimIndent())
    }

    override fun invocationGlsl(varName: String): String {
        return "${getVarName(varName)} = ds_${varName}_getPixelIndex(gl_FragCoord.xy)"
    }

    companion object : DataSourceBuilder<PixelIndexDataSource> {
        override val resourceName: String
            get() = "PixelIndex"
        override val contentType: ContentType
            get() = ContentType.PixelIndex
        override val serializerRegistrar: SerializerRegistrar<PixelIndexDataSource>
            get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean = false
        override fun build(inputPort: InputPort): PixelIndexDataSource =
            PixelIndexDataSource()
    }
}

class PixelIndexFeed(
    private val id: String,
    private val textureUniformId: String,
    private val refCounter: RefCounter = RefCounter()
) : Feed, RefCounted by refCounter {

    override fun bind(gl: GlContext): EngineFeed = EngineFeed(gl)

    inner class EngineFeed(gl: GlContext) : PerPixelEngineFeed {
        override val buffer = FloatsParamBuffer(id, 1, gl)

        override fun setOnBuffer(renderTarget: RenderTarget) = run {
            if (renderTarget is FixtureRenderTarget) {
                buffer.scoped(renderTarget).also { view ->
                    for (pixelIndex in 0 until renderTarget.pixelCount) {
                        view[pixelIndex] = pixelIndex.toFloat()
                    }
                }
            } else {
                logger.warn { "Attempted to set per-pixel data for a non-FixtureRenderTarget, but that's impossible!" }
            }
            Unit
        }

        override fun bind(glslProgram: GlslProgram) = ProgramFeed(glslProgram)

        inner class ProgramFeed(glslProgram: GlslProgram) : PerPixelProgramFeed(updateMode) {
            override val buffer: ParamBuffer get() = this@EngineFeed.buffer
            override val uniform: Uniform = glslProgram.getUniform(textureUniformId)
                ?: error("no uniform $textureUniformId")
            override val isValid: Boolean get() = true
        }
    }

    override fun release() {
        refCounter.release()
    }

    companion object {
        private val logger = Logger<PixelIndexFeed>()
    }
}

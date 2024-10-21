package baaahs.device

import baaahs.gl.GlContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.PerPixelEngineFeedContext
import baaahs.gl.data.PerPixelProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.param.FloatsParamBuffer
import baaahs.gl.param.ParamBuffer
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ProgramBuilder
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.RenderTarget
import baaahs.gl.shader.InputPort
import baaahs.plugin.SerializerRegistrar
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:PixelIndex")
data class PixelIndexFeed(@Transient val `_`: Boolean = true) : Feed {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Pixel Index"
    override fun getType(): GlslType = GlslType.Int
    override val contentType: ContentType
        get() = ContentType.XyzCoordinate

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
        return PixelIndexFeedContext(getVarName(id), "ds_${id}_texture")
    }

    override fun appendDeclaration(buf: ProgramBuilder, id: String) {
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

    companion object : FeedBuilder<PixelIndexFeed> {
        override val title: String get() = "Pixel Index"
        override val description: String get() = "The index of this pixel within its fixture."
        override val resourceName: String
            get() = "PixelIndex"
        override val contentType: ContentType
            get() = ContentType.PixelIndex
        override val serializerRegistrar: SerializerRegistrar<PixelIndexFeed>
            get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean = false
        override fun build(inputPort: InputPort): PixelIndexFeed =
            PixelIndexFeed()
    }
}

class PixelIndexFeedContext(
    private val id: String,
    private val textureUniformId: String
) : FeedContext, RefCounted by RefCounter() {

    override fun bind(gl: GlContext): EngineFeedContext = EngineFeedContext(gl)

    inner class EngineFeedContext(gl: GlContext) : PerPixelEngineFeedContext {
        override val buffer = FloatsParamBuffer(id, 1, gl)

        override fun setOnBuffer(renderTarget: RenderTarget) = run {
            if (renderTarget is FixtureRenderTarget) {
                buffer.scoped(renderTarget).also { view ->
                    for (pixelIndex in 0 until renderTarget.componentCount) {
                        view[pixelIndex] = pixelIndex.toFloat()
                    }
                }
            } else {
                logger.warn { "Attempted to set per-pixel data for a non-FixtureRenderTarget, but that's impossible!" }
            }
            Unit
        }

        override fun bind(glslProgram: GlslProgram) = ProgramFeedContext(glslProgram)

        inner class ProgramFeedContext(glslProgram: GlslProgram) : PerPixelProgramFeedContext(updateMode) {
            override val buffer: ParamBuffer get() = this@EngineFeedContext.buffer
            override val textureUniform = glslProgram.getTextureUniform(textureUniformId)
                ?: error("no uniform $textureUniformId")
            override val isValid: Boolean get() = true
        }
    }

    companion object {
        private val logger = Logger<PixelIndexFeedContext>()
    }
}

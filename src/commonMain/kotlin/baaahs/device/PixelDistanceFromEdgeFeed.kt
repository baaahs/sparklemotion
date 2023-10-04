package baaahs.device

import baaahs.geom.Vector3F
import baaahs.gl.GlContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.PerPixelEngineFeedContext
import baaahs.gl.data.PerPixelProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.param.FloatsParamBuffer
import baaahs.gl.param.ParamBuffer
import baaahs.gl.patch.ContentType
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.RenderTarget
import baaahs.gl.shader.InputPort
import baaahs.model.Model
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
import kotlin.math.min

@Serializable
@SerialName("baaahs.Core:PixelDistanceFromEdge")
data class PixelDistanceFromEdgeFeed(@Transient val `_`: Boolean = true) : Feed {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Pixel Distance from Edge"
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType
        get() = ContentType.Float

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
        return PixelDistanceFromEdgeFeedContext(getVarName(id), "ds_${id}_texture")
    }

    override fun appendDeclaration(buf: StringBuilder, id: String) {
        val textureUniformId = "ds_${id}_texture"
        val varName = getVarName(id)
        buf.append("""
            uniform sampler2D $textureUniformId;
            float ds_${id}_getPixelDistanceFromEdge(vec2 rasterCoord) {
                return texelFetch($textureUniformId, ivec2(rasterCoord.xy), 0).x;
            }
            float $varName;
            
        """.trimIndent())
    }

    override fun invocationGlsl(varName: String): String {
        return "${getVarName(varName)} = ds_${varName}_getPixelDistanceFromEdge(gl_FragCoord.xy)"
    }

    companion object : FeedBuilder<PixelDistanceFromEdgeFeed> {
        override val title: String get() = "Pixel Distance from Edge"
        override val description: String get() = "The distance of this pixel to the nearest edge of its container."
        override val resourceName: String
            get() = "PixelDistanceFromEdge"
        override val contentType: ContentType
            get() = ContentType.Float
        override val serializerRegistrar: SerializerRegistrar<PixelDistanceFromEdgeFeed>
            get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): PixelDistanceFromEdgeFeed =
            PixelDistanceFromEdgeFeed()
    }
}

class PixelDistanceFromEdgeFeedContext(
    private val id: String,
    private val textureUniformId: String
) : FeedContext, RefCounted by RefCounter() {

    override fun bind(gl: GlContext): EngineFeedContext = EngineFeedContext(gl)

    inner class EngineFeedContext(gl: GlContext) : PerPixelEngineFeedContext {
        override val buffer = FloatsParamBuffer(id, 1, gl)

        override fun setOnBuffer(renderTarget: RenderTarget) = run {
            val fixture = renderTarget.fixture
            if (renderTarget is FixtureRenderTarget && fixture.fixtureConfig is PixelArrayDevice.Config) {
                val pixelLocations = fixture.fixtureConfig.pixelLocations
                val surface = fixture.modelEntity as? Model.Surface
                val lines = surface?.lines

                buffer.scoped(renderTarget).also { view ->
                    for (pixelIndex in 0 until min(pixelLocations.size, renderTarget.componentCount)) {
                        val distanceFromEdge = lines?.let { lines ->
                            val pixelLocation = pixelLocations[pixelIndex] ?: Vector3F.unknown

                            lines.mapNotNull { line ->
                                line.shortestDistanceTo(pixelLocation)
                            }.minOrNull()
                        } ?: 0f

                        view[pixelIndex, 0] = distanceFromEdge
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
        private val logger = Logger<PixelDistanceFromEdgeFeedContext>()
    }
}
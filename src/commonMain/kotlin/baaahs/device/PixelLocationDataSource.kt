package baaahs.device

import baaahs.ShowPlayer
import baaahs.fixtures.PixelArrayFixture
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
import baaahs.glsl.Uniform
import baaahs.plugin.SerializerRegistrar
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.plugin.core.FixtureInfoDataSource
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.min

@Serializable
@SerialName("baaahs.Core:PixelLocation")
data class PixelLocationDataSource(@Transient val `_`: Boolean = true) : DataSource {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Pixel Location"
    override fun getType(): GlslType = GlslType.Vec3
    override val contentType: ContentType
        get() = ContentType.XyzCoordinate

    override val dependencies: Map<String, DataSource>
        get() = mapOf("fixtureInfo" to FixtureInfoDataSource())

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
        return PixelLocationFeedContext(getVarName(id), "ds_${id}_texture")
    }

    override fun appendDeclaration(buf: StringBuilder, id: String) {
        val textureUniformId = "ds_${id}_texture"
        val varName = getVarName(id)

        /**language=glsl*/
        buf.append("""
            uniform sampler2D $textureUniformId;
            vec3 ds_${id}_getPixelCoords(vec2 rasterCoord) {
                vec3 xyzInEntity = texelFetch($textureUniformId, ivec2(rasterCoord.xy), 0).xyz;
                vec4 xyzwInModel = in_fixtureInfo.transformation * vec4(xyzInEntity, 1.);
                return xyzwInModel.xyz;
            }
            vec3 $varName;
            
        """.trimIndent())
    }

    override fun invocationGlsl(varName: String): String {
        return "${getVarName(varName)} = ds_${varName}_getPixelCoords(gl_FragCoord.xy)"
    }

    companion object : DataSourceBuilder<PixelLocationDataSource> {
        override val title: String get() = "Pixel Location"
        override val description: String get() = "The location of this pixel within the model entity."
        override val resourceName: String
            get() = "PixelLocation"
        override val contentType: ContentType
            get() = ContentType.XyzCoordinate
        override val serializerRegistrar: SerializerRegistrar<PixelLocationDataSource>
            get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): PixelLocationDataSource =
            PixelLocationDataSource()
    }
}

class PixelLocationFeedContext(
    private val id: String,
    private val textureUniformId: String
) : FeedContext, RefCounted by RefCounter() {

    override fun bind(gl: GlContext): EngineFeedContext = EngineFeedContext(gl)

    inner class EngineFeedContext(gl: GlContext) : PerPixelEngineFeedContext {
        override val buffer = FloatsParamBuffer(id, 3, gl)

        override fun setOnBuffer(renderTarget: RenderTarget) = run {
            if (renderTarget is FixtureRenderTarget) {
                val fixture = renderTarget.fixture
                if (fixture is PixelArrayFixture) {
                    val pixelLocations = fixture.pixelLocations

                    buffer.scoped(renderTarget).also { view ->
                        for (pixelIndex in 0 until min(pixelLocations.size, renderTarget.componentCount)) {
                            val location = pixelLocations[pixelIndex]
                            view[pixelIndex, 0] = location.x
                            view[pixelIndex, 1] = location.y
                            view[pixelIndex, 2] = location.z
                        }
                    }
                } else {
                    logger.warn { "Attempted to set per-pixel data for a non-PixelArrayFixture, but that's impossible!" }
                }
            } else {
                logger.warn { "Attempted to set per-pixel data for a non-FixtureRenderTarget, but that's impossible!" }
            }
            Unit
        }

        override fun bind(glslProgram: GlslProgram) = ProgramFeedContext(glslProgram)

        inner class ProgramFeedContext(glslProgram: GlslProgram) : PerPixelProgramFeedContext(updateMode) {
            override val buffer: ParamBuffer get() = this@EngineFeedContext.buffer
            override val uniform: Uniform = glslProgram.getUniform(textureUniformId)
                ?: error("no uniform $textureUniformId")
            override val isValid: Boolean get() = true
        }
    }

    companion object {
        private val logger = Logger<PixelLocationFeedContext>()
    }
}

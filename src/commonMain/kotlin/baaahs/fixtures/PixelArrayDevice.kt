package baaahs.fixtures

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.*
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
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
import baaahs.show.Shader
import baaahs.util.Logger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.min

object PixelArrayDevice : DeviceType {
    override val id: String get() = "PixelArray"
    override val title: String get() = "Pixel Array"

    override val dataSourceBuilders: List<DataSourceBuilder<*>>
        get() = listOf(PixelLocationDataSource)

    override val resultParams: List<ResultParam> = listOf(
        ResultParam("Pixel Color", ColorResultType)
    )

    override val resultContentType: ContentType
        get() = ContentType.Color
    override val likelyPipelines: List<Pair<ContentType, ContentType>>
        get() = with(ContentType) {
            listOf(
                XyzCoordinate to UvCoordinate,
                UvCoordinate to Color
            )
        }

    override val errorIndicatorShader: Shader
        get() = Shader(
            "Ω Guru Meditation Error Ω",
            /**language=glsl*/
            """
                uniform float time;
                void main() {
                    gl_FragColor = (mod(time, 2.) < 1.)
                        ? vec4(.75, 0., 0., 1.)
                        : vec4(.25, 0., 0., 1.);
                }
            """.trimIndent()
        )


    fun getColorResults(resultViews: List<ResultView>) =
        resultViews[0] as ColorResultType.ColorResultView

    override fun toString(): String = id
}

@Serializable
@SerialName("baaahs.Core:PixelLocation")
data class PixelLocationDataSource(@Transient val `_`: Boolean = true) : DataSource {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Pixel Location"
    override fun getType(): GlslType = GlslType.Vec3
    override val contentType: ContentType
        get() = ContentType.XyzCoordinate

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        return PixelLocationFeed(getVarName(id), "ds_${id}_texture")
    }

    override fun appendDeclaration(buf: StringBuilder, id: String) {
        val textureUniformId = """ds_${id}_texture"""
        val varName = getVarName(id)
        buf.append("""
            uniform sampler2D $textureUniformId;
            vec3 ds_${id}_getPixelCoords(vec2 rasterCoord) {
                return texelFetch($textureUniformId, ivec2(rasterCoord.xy), 0).xyz;
            }
            vec3 $varName;
            
        """.trimIndent())
    }

    override fun invocationGlsl(varName: String): String {
        return "${getVarName(varName)} = ds_${varName}_getPixelCoords(gl_FragCoord.xy)"
    }

    companion object : DataSourceBuilder<PixelLocationDataSource> {
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

class PixelLocationFeed(
    private val id: String,
    private val textureUniformId: String,
    private val refCounter: RefCounter = RefCounter()
) : Feed, RefCounted by refCounter {

    override fun bind(gl: GlContext): EngineFeed = EngineFeed(gl)

    inner class EngineFeed(gl: GlContext) : PerPixelEngineFeed {
        override val buffer = FloatsParamBuffer(id, 3, gl)

        override fun setOnBuffer(renderTarget: RenderTarget) = run {
            if (renderTarget is FixtureRenderTarget) {
                val pixelLocations = renderTarget.fixture.pixelLocations
                buffer.scoped(renderTarget).also { view ->
                    for (pixelIndex in 0 until min(pixelLocations.size, renderTarget.pixelCount)) {
                        val location = pixelLocations[pixelIndex]
                        view[pixelIndex, 0] = location.x
                        view[pixelIndex, 1] = location.y
                        view[pixelIndex, 2] = location.z
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
        private val logger = Logger<PixelLocationFeed>()
    }
}
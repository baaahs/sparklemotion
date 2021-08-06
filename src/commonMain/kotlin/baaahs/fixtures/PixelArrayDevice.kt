package baaahs.fixtures

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.geom.Vector3F
import baaahs.gl.GlContext
import baaahs.gl.data.*
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.RenderResults
import baaahs.gl.render.RenderTarget
import baaahs.gl.render.ResultStorage
import baaahs.gl.shader.InputPort
import baaahs.glsl.SurfacePixelStrategy
import baaahs.glsl.Uniform
import baaahs.model.Model
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
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.math.min

object PixelArrayDevice : DeviceType {
    override val id: String get() = "PixelArray"
    override val title: String get() = "Pixel Array"

    override val dataSourceBuilders: List<DataSourceBuilder<*>>
        get() = listOf(PixelLocationDataSource)

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

    override val defaultConfig: FixtureConfig
        get() = Config(null, PixelFormat.RGB8)
    override val serialModule: SerializersModule
        get() = SerializersModule {
            polymorphic(FixtureConfig::class) {
                subclass(Config::class, Config.serializer())
            }
        }

    override fun createResultStorage(renderResults: RenderResults): ResultStorage {
        val resultBuffer = renderResults.allocate("Pixel Color", ColorResultType)
        return SingleResultStorage(resultBuffer)
    }

    override fun toString(): String = id

    @Serializable
    data class Config(
        val pixelCount: Int? = null,
        val pixelFormat: PixelFormat,
        val gammaCorrection: Float = 1f,
        val pixelArrangement: SurfacePixelStrategy? = null
    ) : FixtureConfig {
        override val deviceType: DeviceType
            get() = PixelArrayDevice

        override fun generatePixelLocations(pixelCount: Int, entity: Model.Entity?, model: Model): List<Vector3F>? {
            return pixelArrangement?.forFixture(pixelCount, entity, model)
        }

        fun writeData(results: ColorResultType.ColorFixtureResults): ByteArray {
            return pixelFormat.writeData(results)
        }
    }

    enum class PixelFormat {
        RGB8 {
            override val bytesPerPixel: Int = 3

            override fun writeData(results: ColorResultType.ColorFixtureResults): ByteArray {
                val pixelCount = results.pixelCount
                val buf = ByteArray(pixelCount * 3)
                var j = 0
                for (i in 0 until pixelCount) {
                    val color = results[i]

                    buf[j++] = color.redB
                    buf[j++] = color.greenB
                    buf[j++] = color.blueB
                }
                return buf
            }
        },
        GRB8 {
            override val bytesPerPixel: Int = 3

            override fun writeData(results: ColorResultType.ColorFixtureResults): ByteArray {
                val pixelCount = results.pixelCount
                val buf = ByteArray(pixelCount * 3)
                var j = 0
                for (i in 0 until pixelCount) {
                    val color = results[i]

                    buf[j++] = color.greenB
                    buf[j++] = color.redB
                    buf[j++] = color.blueB
                }
                return buf
            }
        };

        abstract val bytesPerPixel: Int
        abstract fun writeData(results: ColorResultType.ColorFixtureResults): ByteArray
    }
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
        val textureUniformId = "ds_${id}_texture"
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
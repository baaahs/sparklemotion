package baaahs.device

import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.SingleResultStorage
import baaahs.geom.Vector3F
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderResults
import baaahs.gl.render.ResultStorage
import baaahs.gl.result.ColorResultType
import baaahs.glsl.SurfacePixelStrategy
import baaahs.model.Model
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

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

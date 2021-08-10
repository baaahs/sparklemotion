package baaahs.device

import baaahs.Color
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.SingleResultStorage
import baaahs.geom.Vector3F
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderResults
import baaahs.gl.render.ResultStorage
import baaahs.gl.result.ColorResultType
import baaahs.glsl.SurfacePixelStrategy
import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader
import baaahs.sim.FixtureSimulation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object PixelArrayDevice : DeviceType {
    override val id: String get() = "PixelArray"
    override val title: String get() = "Pixel Array"

    override val dataSourceBuilders: List<DataSourceBuilder<*>>
        get() = listOf(
            PixelLocationDataSource,
            PixelIndexDataSource,
            PixelCountDataSource,
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

    @Serializable @SerialName("baaahs.Core:PixelArrayDevice")
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
            val pixelCount = results.pixelCount
            val bytesPerPixel = pixelFormat.channelsPerPixel
            val buf = ByteArray(pixelCount * bytesPerPixel)
            for (i in 0 until pixelCount) {
                pixelFormat.writeColor(results[i], buf, i * bytesPerPixel)
            }
            return buf
        }

        override fun receiveRemoteVisualizationFixtureInfo(
            reader: ByteArrayReader,
            fixtureSimulation: FixtureSimulation
        ) {
            val pixelCount = reader.readInt()
            val pixelLocations = (0 until pixelCount).map {
                Vector3F.parse(reader)
            }.toTypedArray()

            fixtureSimulation.updateVisualizerWith(this, pixelCount, pixelLocations)
        }
    }

    enum class PixelFormat {
        RGB8 {
            override val channelsPerPixel: Int = 3

            override fun readColor(reader: ByteArrayReader): Color {
                return Color.parseWithoutAlpha(reader)
            }

            override fun writeColor(color: Color, buf: ByteArray, i: Int) {
                buf[i] = color.redB
                buf[i + 1] = color.greenB
                buf[i + 2] = color.blueB
            }
        },
        GRB8 {
            override val channelsPerPixel: Int = 3

            override fun readColor(reader: ByteArrayReader): Color {
                val greenB = reader.readByte()
                val redB = reader.readByte()
                val blueB = reader.readByte()
                return Color(redB, greenB, blueB)
            }

            override fun writeColor(color: Color, buf: ByteArray, i: Int) {
                buf[i] = color.greenB
                buf[i + 1] = color.redB
                buf[i + 2] = color.blueB
            }
        };

        abstract val channelsPerPixel: Int
        abstract fun readColor(reader: ByteArrayReader): Color
        abstract fun writeColor(color: Color, buf: ByteArray, i: Int)
    }
}

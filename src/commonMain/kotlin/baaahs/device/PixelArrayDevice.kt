package baaahs.device

import baaahs.Color
import baaahs.fixtures.FixtureConfig
import baaahs.geom.Vector3F
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderResults
import baaahs.gl.result.ColorResultType
import baaahs.gl.result.ResultStorage
import baaahs.gl.result.SingleResultStorage
import baaahs.glsl.SurfacePixelStrategy
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
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

    @Serializable @SerialName("PixelArray")
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

            override fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit) {
                val redF = reader.readByte().asUnsignedToInt() / 255f
                val greenF = reader.readByte().asUnsignedToInt() / 255f
                val blueF = reader.readByte().asUnsignedToInt() / 255f
                setter(redF, greenF, blueF)
            }

            override fun writeColor(color: Color, buf: ByteArrayWriter) {
                buf.writeByte(color.redB)
                buf.writeByte(color.greenB)
                buf.writeByte(color.blueB)
            }
        },
        GRB8 {
            override val channelsPerPixel: Int = 3

            override fun readColor(reader: ByteArrayReader): Color {
                val greenB = reader.readByte()
                val redB = reader.readByte()
                val blueB = reader.readByte()

                // Using Color's int constructor fixes a bug in Safari causing
                // color values above 127 to be treated as 0. Untested. :-(
                return Color(
                    redB.toInt() and 0xff,
                    greenB.toInt() and 0xff,
                    blueB.toInt() and 0xff
                )
            }

            override fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit) {
                val greenF = reader.readByte().asUnsignedToInt() / 255f
                val redF = reader.readByte().asUnsignedToInt() / 255f
                val blueF = reader.readByte().asUnsignedToInt() / 255f
                setter(redF, greenF, blueF)
            }

            override fun writeColor(color: Color, buf: ByteArrayWriter) {
                buf.writeByte(color.greenB)
                buf.writeByte(color.redB)
                buf.writeByte(color.blueB)
            }
        };

        abstract val channelsPerPixel: Int
        abstract fun readColor(reader: ByteArrayReader): Color
        abstract fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit)
        abstract fun writeColor(color: Color, buf: ByteArrayWriter)
    }
}

private fun Byte.asUnsignedToInt(): Int = this.toInt().and(0xFF)

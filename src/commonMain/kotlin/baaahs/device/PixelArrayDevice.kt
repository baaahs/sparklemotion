package baaahs.device

import baaahs.Color
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.PixelArrayFixture
import baaahs.fixtures.Transport
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
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureConfig
import baaahs.scene.MutableFixtureMapping
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader
import baaahs.ui.View
import baaahs.visualizer.visualizerBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object PixelArrayDevice : FixtureType {
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

    override val emptyConfig: FixtureConfig
        get() = Config()
    override val defaultConfig: FixtureConfig
        get() = Config(null, PixelFormat.default, 1f)
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

    override fun createFixture(
        modelEntity: Model.Entity?,
        componentCount: Int,
        fixtureConfig: FixtureConfig,
        name: String,
        transport: Transport,
        model: Model
    ): Fixture {
        fixtureConfig as Config

        val pixelLocations = fixtureConfig.pixelLocations
            ?.map { it ?: Vector3F(0f, 0f, 0f) }
            ?: fixtureConfig.generatePixelLocations(componentCount, modelEntity, model)
            ?: emptyList()

        return PixelArrayFixture(
            modelEntity, componentCount, name, transport,
            fixtureConfig.pixelFormat ?: error("No pixel format specified."),
            fixtureConfig.gammaCorrection ?: error("No gamma correction specified."),
            pixelLocations
        )
    }

    override fun toString(): String = id

    @Serializable @SerialName("PixelArray")
    data class Config(
        val pixelCount: Int? = null,
        val pixelFormat: PixelFormat? = null,
        val gammaCorrection: Float? = null,
        val pixelArrangement: SurfacePixelStrategy? = null,
        val pixelLocations: List<Vector3F?>? = null
    ) : FixtureConfig {
        override val componentCount: Int?
            get() = pixelCount

        override val fixtureType: FixtureType
            get() = PixelArrayDevice

        override fun edit(): MutableFixtureConfig = MutableConfig(this)

        /** Merges two configs, preferring values from [other]. */
        override fun plus(other: FixtureConfig?): FixtureConfig =
            if (other == null) this
            else plus(other as Config)

        /** Merges two configs, preferring values from [other]. */
        operator fun plus(other: Config): Config = Config(
            other.componentCount ?: componentCount,
            other.pixelFormat ?: pixelFormat,
            other.gammaCorrection ?: gammaCorrection,
            other.pixelArrangement ?: pixelArrangement,
            other.pixelLocations ?: pixelLocations
        )

        override fun generatePixelLocations(pixelCount: Int, entity: Model.Entity?, model: Model): List<Vector3F>? {
            return pixelArrangement?.forFixture(pixelCount, entity, model)
        }
    }

    class MutableConfig(config: Config) : MutableFixtureConfig {
        override val fixtureType: FixtureType
            get() = PixelArrayDevice

        var componentCount: Int? = config.componentCount
        var pixelFormat: PixelFormat? = config.pixelFormat
        var gammaCorrection: Float? = config.gammaCorrection
        var pixelArrangement: SurfacePixelStrategy? = config.pixelArrangement

        override fun build(): FixtureConfig =
            Config(componentCount, pixelFormat, gammaCorrection, pixelArrangement)

        override fun getEditorView(
            editingController: EditingController<*>,
            mutableFixtureMapping: MutableFixtureMapping
        ): View = visualizerBuilder.getPixelArrayFixtureConfigEditorView(editingController, mutableFixtureMapping)
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
        RBG8 {
            override val channelsPerPixel: Int = 3

            override fun readColor(reader: ByteArrayReader): Color {
                val redB = reader.readByte()
                val blueB = reader.readByte()
                val greenB = reader.readByte()
                return Color(redB, greenB, blueB)
            }

            override fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit) {
                val redF = reader.readByte().asUnsignedToInt() / 255f
                val blueF = reader.readByte().asUnsignedToInt() / 255f
                val greenF = reader.readByte().asUnsignedToInt() / 255f
                setter(redF, greenF, blueF)
            }

            override fun writeColor(color: Color, buf: ByteArrayWriter) {
                buf.writeByte(color.redB)
                buf.writeByte(color.blueB)
                buf.writeByte(color.greenB)
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

        companion object {
            val default = RGB8
        }
    }
}

private fun Byte.asUnsignedToInt(): Int = this.toInt().and(0xFF)

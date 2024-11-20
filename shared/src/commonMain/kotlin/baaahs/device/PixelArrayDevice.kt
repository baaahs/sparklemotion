package baaahs.device

import baaahs.fixtures.ConfigPreview
import baaahs.fixtures.ConfigPreviewNugget
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.FixtureOptions
import baaahs.geom.Vector3F
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderResults
import baaahs.gl.result.ColorResultType
import baaahs.gl.result.ResultStorage
import baaahs.gl.result.SingleResultStorage
import baaahs.glsl.SurfacePixelStrategy
import baaahs.model.Model
import baaahs.model.PixelArray
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureOptions
import baaahs.show.FeedBuilder
import baaahs.show.Shader
import baaahs.ui.View
import baaahs.visualizer.entity.visualizerBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object PixelArrayDevice : PixelArrayFixtureType() {
    @Serializable
    @SerialName("PixelArray")
    data class Options(
        val pixelCount: Int? = null,
        val pixelFormat: PixelFormat? = null,
        val gammaCorrection: Float? = null,
        val pixelArrangement: SurfacePixelStrategy? = null,
        val pixelLocations: List<Vector3F?>? = null
    ) : FixtureOptions {
        override val componentCount: Int?
            get() = pixelCount

        override val bytesPerComponent: Int
            get() = pixelFormat?.channelsPerPixel ?: error("no pixel format specified")

        override val fixtureType: FixtureType
            get() = PixelArrayDevice

        override fun edit(): MutableFixtureOptions =
            MutableOptions(componentCount, pixelFormat, gammaCorrection, pixelArrangement)

        private fun generatePixelLocations(pixelCount: Int, entity: Model.Entity?, model: Model): List<Vector3F>? =
            pixelArrangement?.forFixture(pixelCount, entity, model)

        /** Merges two options, preferring values from [other]. */
        override fun plus(other: FixtureOptions?): FixtureOptions =
            if (other == null) this
            else plus(other as Options)

        /** Merges two options, preferring values from [other]. */
        operator fun plus(other: Options): Options = Options(
            other.componentCount ?: componentCount,
            other.pixelFormat ?: pixelFormat,
            other.gammaCorrection ?: gammaCorrection,
            other.pixelArrangement ?: pixelArrangement,
            other.pixelLocations ?: pixelLocations
        )

        override fun preview(): ConfigPreview = object : ConfigPreview {
            override fun summary(): List<ConfigPreviewNugget> = listOf(
                ConfigPreviewNugget("Pixel Count", pixelCount?.toString(), "px"),
                ConfigPreviewNugget("Pixel Format", pixelFormat?.name),
                ConfigPreviewNugget("Gamma Correction", gammaCorrection?.toString(), "gamma")
            )
        }

        override fun toConfig(entity: Model.Entity?, model: Model, defaultComponentCount: Int?): FixtureConfig {
            val pixelCount = componentCount ?: defaultComponentCount ?: error("Component count not specified.")
            val pixelArray = entity as? PixelArray
            return Config(
                pixelCount,
                pixelFormat ?: error("Pixel format not specified."),
                gammaCorrection ?: error("Gamma correction not specified."),
                EnumeratedPixelLocations(
                    pixelLocations
                        ?: pixelArray?.calculatePixelLocalLocations(pixelCount)
                        ?: generatePixelLocations(pixelCount, entity, model)
                        ?: emptyList()
                )
            )
        }
    }

    class MutableOptions(
        var componentCount: Int?,
        var pixelFormat: PixelFormat?,
        var gammaCorrection: Float?,
        var pixelArrangement: SurfacePixelStrategy?
    ) : MutableFixtureOptions {
        override val fixtureType: FixtureType
            get() = PixelArrayDevice

        override fun build(): FixtureOptions =
            Options(componentCount, pixelFormat, gammaCorrection, pixelArrangement)

        override fun getEditorView(
            editingController: EditingController<*>
        ): View = visualizerBuilder.getPixelArrayFixtureOptionsEditorView(editingController, this)
    }

    @Serializable
    @SerialName("PixelArray")
    data class Config(
        val pixelCount: Int,
        val pixelFormat: PixelFormat,
        val gammaCorrection: Float,
        val pixelLocations: PixelLocations
    ) : FixtureConfig {
        override val componentCount: Int
            get() = pixelCount
        override val bytesPerComponent: Int
            get() = pixelFormat.channelsPerPixel
        override val fixtureType: FixtureType
            get() = PixelArrayDevice
    }
}

interface PixelLocations {
    operator fun get(index: Int): Vector3F?
    val size: Int

    fun arrayOfVector3F() = Array(size) { get(it) ?: Vector3F.unknown }}

@Serializable @SerialName("enumerated")
data class EnumeratedPixelLocations(
    private val locations: List<Vector3F?>
) : PixelLocations {
    override fun get(index: Int): Vector3F? = locations[index]
    override val size: Int get() = locations.size

    constructor(vararg locations: Vector3F?) : this(locations.toList())
}

open class PixelArrayFixtureType : FixtureType {
    override val id: String get() = "PixelArray"
    override val title: String get() = "Pixel Array"

    override val feedBuilders: List<FeedBuilder<*>>
        get() = listOf(
            PixelLocationFeed,
            PixelIndexFeed,
            PixelCountFeed,
            PixelDistanceFromEdgeFeed
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

    override val emptyOptions: FixtureOptions
        get() = PixelArrayDevice.Options()
    override val defaultOptions: FixtureOptions
        get() = PixelArrayDevice.Options(null, PixelFormat.default, 1f)
    override val serialModule: SerializersModule
        get() = SerializersModule {
            polymorphic(FixtureOptions::class) {
                subclass(PixelArrayDevice.Options::class, PixelArrayDevice.Options.serializer())
            }
            polymorphic(FixtureConfig::class) {
                subclass(PixelArrayDevice.Config::class, PixelArrayDevice.Config.serializer())
            }
        }

    override fun createResultStorage(renderResults: RenderResults): ResultStorage {
        val resultBuffer = renderResults.allocate("Pixel Color", ColorResultType)
        return SingleResultStorage(resultBuffer)
    }

    override fun toString(): String = id
}
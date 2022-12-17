package baaahs.device

import baaahs.fixtures.*
import baaahs.geom.Vector3F
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderResults
import baaahs.gl.result.ColorResultType
import baaahs.gl.result.ResultStorage
import baaahs.gl.result.SingleResultStorage
import baaahs.glsl.SurfacePixelStrategy
import baaahs.model.Model
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureConfig
import baaahs.show.FeedBuilder
import baaahs.show.Shader
import baaahs.ui.View
import baaahs.visualizer.visualizerBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object PixelArrayDevice : PixelArrayFixtureType() {
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

        override val bytesPerComponent: Int
            get() = pixelFormat?.channelsPerPixel ?: error("no pixel format specified")

        override val fixtureType: FixtureType
            get() = PixelArrayDevice

        override fun edit(): MutableFixtureConfig = MutableConfig(this)

        override fun generatePixelLocations(pixelCount: Int, entity: Model.Entity?, model: Model): List<Vector3F>? =
            pixelArrangement?.forFixture(pixelCount, entity, model)

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

        override fun preview(): ConfigPreview = object : ConfigPreview {
            override fun summary(): List<Pair<String, String?>> = listOf(
                "Pixel Count" to pixelCount?.toString(),
                "Pixel Format" to pixelFormat?.name,
                "Gamma Correction" to gammaCorrection?.toString()
            )
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
            editingController: EditingController<*>
        ): View = visualizerBuilder.getPixelArrayFixtureConfigEditorView(editingController, this)
    }
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

    override val emptyConfig: FixtureConfig
        get() = PixelArrayDevice.Config()
    override val defaultConfig: FixtureConfig
        get() = PixelArrayDevice.Config(null, PixelFormat.default, 1f)
    override val serialModule: SerializersModule
        get() = SerializersModule {
            polymorphic(FixtureConfig::class) {
                subclass(PixelArrayDevice.Config::class, PixelArrayDevice.Config.serializer())
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
        fixtureConfig as PixelArrayDevice.Config

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
}
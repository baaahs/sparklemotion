package baaahs.device

import baaahs.fixtures.ConfigPreview
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.FixtureOptions
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderRegime
import baaahs.gl.render.RenderResults
import baaahs.gl.result.ResultStorage
import baaahs.gl.result.SingleResultStorage
import baaahs.model.Model
import baaahs.plugin.core.MovingHeadParams
import baaahs.scene.MutableFixtureOptions
import baaahs.show.FeedBuilder
import baaahs.show.Shader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object ProjectorDevice : FixtureType {
    override val id: String get() = "Projector"
    override val title: String get() = "Projector"

    override val feedBuilders: List<FeedBuilder<*>>
        get() = listOf(PixelLocationFeed)

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
                        ? vec4(.45, .45, 0., 0.)
                        : vec4(.55, .55, 0., 0.);
                }
            """.trimIndent()
        )
    override val emptyOptions: FixtureOptions
        get() = Options(null, null, null)
    override val defaultOptions: FixtureOptions
        get() = emptyOptions
    override val serialModule: SerializersModule
        get() = SerializersModule {
            polymorphic(FixtureOptions::class) {
                subclass(Options::class, Options.serializer())
            }
        }
    override val renderRegime: RenderRegime
        get() = RenderRegime.Raster

    override fun createResultStorage(renderResults: RenderResults): ResultStorage {
        val resultBuffer = renderResults.allocate("Moving Head Params", MovingHeadParams.resultType)
        return SingleResultStorage(resultBuffer)
    }

    override fun toString(): String = id

    @Serializable @SerialName("Projector")
    data class Options(val monitorName: String?, val width: Int?, val height: Int?) : FixtureOptions {
        override val componentCount: Int
            get() = 1
        override val bytesPerComponent: Int
            get() = 0

        override val fixtureType: FixtureType
            get() = ProjectorDevice

        override fun edit(): MutableFixtureOptions = TODO() // MutableConfig(this)

        override fun plus(other: FixtureOptions?): FixtureOptions =
            if (other == null) this
            else plus(other as Options)

        operator fun plus(other: Options): Options = Options(
            other.monitorName ?: monitorName,
            other.width ?: width,
            other.height ?: height
        )

        override fun preview(): ConfigPreview = object : ConfigPreview {
            override fun summary(): List<Pair<String, String?>> =
                listOf(
                    "Monitor" to monitorName,
                    "Width" to width?.toString(),
                    "Height" to height?.toString()
                )
        }

        override fun toConfig(entity: Model.Entity?, model: Model, defaultComponentCount: Int?): FixtureConfig {
            TODO("not implemented")
        }
    }

    data class Config (
        val monitorName: String,
        val width: Int,
        val height: Int
    ) : FixtureConfig {
        override val componentCount: Int
            get() = TODO("not implemented")
        override val bytesPerComponent: Int
            get() = TODO("not implemented")
        override val fixtureType: FixtureType
            get() = TODO("not implemented")
    }
}
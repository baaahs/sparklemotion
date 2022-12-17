package baaahs.device

import baaahs.dmx.Shenzarpy
import baaahs.fixtures.*
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderResults
import baaahs.gl.result.ResultStorage
import baaahs.gl.result.SingleResultStorage
import baaahs.model.Model
import baaahs.model.MovingHeadAdapter
import baaahs.plugin.core.FixtureInfoFeed
import baaahs.plugin.core.MovingHeadParams
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureConfig
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader
import baaahs.ui.View
import baaahs.visualizer.visualizerBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object MovingHeadDevice : FixtureType {
    override val id: String get() = "MovingHead"
    override val title: String get() = "Moving Head"

    override val dataSourceBuilders: List<DataSourceBuilder<*>>
        get() = listOf(FixtureInfoFeed)

    override val resultContentType: ContentType
        get() = MovingHeadParams.contentType

    override val likelyPipelines: List<Pair<ContentType, ContentType>>
        get() = with(ContentType) {
            listOf(XyzCoordinate to MovingHeadParams.contentType)
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
    override val emptyConfig: FixtureConfig
        get() = Config(null)
    override val defaultConfig: FixtureConfig
        get() = Config(Shenzarpy)
    override val serialModule: SerializersModule
        get() = SerializersModule {
            polymorphic(FixtureConfig::class) {
                subclass(Config::class, Config.serializer())
            }
        }

    override fun createResultStorage(renderResults: RenderResults): ResultStorage {
        val resultBuffer = renderResults.allocate("Moving Head Params", MovingHeadParams.resultType)
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
        return MovingHeadFixture(
            modelEntity, componentCount, name, transport,
            fixtureConfig.adapter ?: error("No adapter specified."))
    }

    override fun toString(): String = id

    @Serializable @SerialName("MovingHead")
    data class Config(val adapter: MovingHeadAdapter?) : FixtureConfig {
        override val componentCount: Int
            get() = 1
        override val bytesPerComponent: Int
            get() = adapter?.dmxChannelCount ?: error("no adapter specified")

        override val fixtureType: FixtureType
            get() = MovingHeadDevice

        override fun edit(): MutableFixtureConfig = MutableConfig(this)

        override fun plus(other: FixtureConfig?): FixtureConfig =
            if (other == null) this
            else plus(other as Config)

        operator fun plus(other: Config): Config = Config(
            other.adapter ?: adapter,
        )

        override fun preview(): ConfigPreview = object : ConfigPreview {
            override fun summary(): List<Pair<String, String?>> =
                listOf("Adapter" to adapter?.id)
        }
    }

    class MutableConfig(config: Config) : MutableFixtureConfig {
        override val fixtureType: FixtureType
            get() = MovingHeadDevice

        var adapter: MovingHeadAdapter? = config.adapter

        override fun build(): FixtureConfig = Config(adapter)
        override fun getEditorView(
            editingController: EditingController<*>
        ): View = visualizerBuilder.getMovingHeadFixtureConfigEditorView(editingController, this)
    }
}
package baaahs.device

import baaahs.dmx.Shenzarpy
import baaahs.fixtures.FixtureConfig
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderResults
import baaahs.gl.result.ResultStorage
import baaahs.gl.result.SingleResultStorage
import baaahs.model.MovingHeadAdapter
import baaahs.plugin.core.FixtureInfoDataSource
import baaahs.plugin.core.MovingHeadParams
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object MovingHeadDevice : DeviceType {
    override val id: String get() = "MovingHead"
    override val title: String get() = "Moving Head"

    override val dataSourceBuilders: List<DataSourceBuilder<*>>
        get() = listOf(FixtureInfoDataSource)

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
    override val defaultConfig: FixtureConfig
        get() = Config(Shenzarpy)
    override val defaultPixelCount: Int
        get() = 1
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

    override fun toString(): String = id

    @Serializable @SerialName("MovingHead")
    data class Config(val adapter: MovingHeadAdapter) : FixtureConfig {
        override val deviceType: DeviceType
            get() = MovingHeadDevice
    }
}
package baaahs.fixtures

import baaahs.gl.patch.ContentType
import baaahs.plugin.core.FixtureInfoDataSource
import baaahs.plugin.core.MovingHeadParams
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader

object MovingHeadDevice : DeviceType {
    override val id: String get() = "MovingHead"
    override val title: String get() = "Moving Head"

    override val dataSourceBuilders: List<DataSourceBuilder<*>>
        get() = listOf(FixtureInfoDataSource)

    override val resultParams: List<ResultParam>
        get() = listOf(
            ResultParam("Moving Head Params", MovingHeadParams.resultType)
        )
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

    fun getResults(resultViews: List<ResultView>) =
        resultViews[0] as MovingHeadParams.ResultView

    override fun toString(): String = id
}
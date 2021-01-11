package baaahs.fixtures

import baaahs.gl.patch.ContentType
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader

object PixelArrayDevice : DeviceType {
    override val id: String get() = "PixelArray"
    override val title: String get() = "Pixel Array"

    override val dataSourceBuilders: List<DataSourceBuilder<*>>
        get() = listOf(PixelLocationDataSource)

    override val resultParams: List<ResultParam> = listOf(
        ResultParam("Pixel Color", ColorResultType)
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


    fun getColorResults(resultViews: List<ResultView>) =
        resultViews[0] as ColorResultType.ColorResultView

    override fun toString(): String = id
}

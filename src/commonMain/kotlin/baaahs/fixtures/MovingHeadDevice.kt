package baaahs.fixtures

import baaahs.gl.patch.ContentType
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.ShaderType

object MovingHeadDevice : DeviceType {
    override val id: String get() = "MovingHead"
    override val title: String get() = "Moving Head"

    override val dataSources: List<DataSource> get() = emptyList()

    override val resultParams: List<ResultParam> get() = listOf(
        ResultParam("Pan/Tilt", Vec2ResultType)
    )
    override val resultContentType: ContentType
        get() = ContentType.PanAndTilt

    override val errorIndicatorShader: Shader
        get() = Shader(
            "Ω Guru Meditation Error Ω",
            ShaderType.Mover,
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
        resultViews[0] as Vec2ResultType.Vec2ResultView

    override fun toString(): String = id
}
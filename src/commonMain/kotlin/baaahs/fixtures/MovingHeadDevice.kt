package baaahs.fixtures

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderTarget
import baaahs.gl.shader.InputPort
import baaahs.model.MovingHead
import baaahs.plugin.CorePlugin
import baaahs.show.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

object MovingHeadDevice : DeviceType {
    override val id: String get() = "MovingHead"
    override val title: String get() = "Moving Head"

    override val dataSources: List<DataSource> get() = listOf(
        MovingHeadInfoDataSource()
    )

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

val movingHeadInfoStruct = GlslCode.GlslStruct(
    "MovingHeadInfo",
    mapOf(
        "location" to GlslType.Vec3,
    ),
    fullText = """
            struct MovingHeadInfo {
                vec3 origin;            
                vec3 heading; // in Euler angles
            }
        """.trimIndent(),
    varName = null
)

val movingHeadInfoType = GlslType.from(movingHeadInfoStruct)
val movingHeadInfoContentType = ContentType("moving-head-info", "Moving Head Info", movingHeadInfoType)

@Serializable
@SerialName("baaahs.Core.MovingHeadInfo")
data class MovingHeadInfoDataSource(@Transient val `_`: Boolean = true) : DataSource {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Moving Head Info"
    override fun getType(): GlslType = movingHeadInfoType
    override val contentType: ContentType
        get() = movingHeadInfoContentType

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        return MovingHeadInfoFeed(getVarName(id))
    }

    companion object : DataSourceBuilder<MovingHeadInfoDataSource> {
        override val resourceName: String get() = "baaahs.Core.MovingHeadInfo"

        override val contentType: ContentType get() = movingHeadInfoContentType

        override fun build(inputPort: InputPort): MovingHeadInfoDataSource {
            return MovingHeadInfoDataSource()
        }
    }
}

class MovingHeadInfoFeed(
    private val id: String,
    private val refCounter: RefCounter = RefCounter()
) : Feed, RefCounted by refCounter {
    override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
        override fun bind(glslProgram: GlslProgram) = object : ProgramFeed {
            override val updateMode: UpdateMode get() = UpdateMode.PER_FIXTURE

            private val originUniform = glslProgram.getUniform("$id.origin")
            private val headingUniform = glslProgram.getUniform("$id.heading")

            override val isValid: Boolean get() = originUniform != null && headingUniform != null

            override fun setOnProgram(renderTarget: RenderTarget) {
                val movingHead = renderTarget.fixture.modelEntity as MovingHead

                originUniform!!.set(movingHead.origin)
                headingUniform!!.set(movingHead.heading)
            }
        }
    }

    override fun release() {
        refCounter.release()
    }
}
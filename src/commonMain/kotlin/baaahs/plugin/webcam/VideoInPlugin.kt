package baaahs.plugin.webcam

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.*
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import com.danielgergely.kgl.GL_LINEAR
import com.danielgergely.kgl.GL_RGBA
import kotlinx.cli.ArgParser
import kotlinx.serialization.SerialName

class VideoInPlugin(private val videoProvider: VideoProvider) : OpenServerPlugin, OpenClientPlugin {
    override val packageName: String = id
    override val title: String = "Video In"

    // We'll just make one up-front. We only ever want one (because equality
    // is using object identity), and there's no overhead.
    internal val videoInDataSource = VideoInFeed()

    override val feedBuilders: List<FeedBuilder<VideoInFeed>>
        get() = listOf(
            object : FeedBuilder<VideoInFeed> {
                override val title get() = "Video Input"
                override val description get() = "Video input."
                override val resourceName get() = "VideoIn"
                override val contentType get() = ContentType.Color
                override val serializerRegistrar
                    get() = objectSerializer("$id:$resourceName", videoInDataSource)

                override fun build(inputPort: InputPort) = videoInDataSource

                override fun funDef(varName: String): String =
                    "vec4 $varName(vec2 uv);"
            }
        )

    @SerialName("baaahs.VideoIn:VideoIn")
    inner class VideoInFeed internal constructor() : Feed {
        override val pluginPackage: String get() = id
        override val title: String get() = "Video In"
        override fun getType(): GlslType = GlslType.Sampler2D
        override val contentType: ContentType get() = ContentType.Color

        override fun appendDeclaration(buf: StringBuilder, id: String) {
            val textureUniformId = "ds_${getVarName(id)}_texture"
            /**language=glsl*/
            buf.append("uniform sampler2D $textureUniformId;\n")
        }

        override fun appendInvoke(buf: StringBuilder, varName: String, inputPort: InputPort) {
            val fn = inputPort.glslArgSite as GlslCode.GlslFunction

            val textureUniformId = "ds_${getVarName(varName)}_texture"
            val uvParamName = fn.params[0].name
            buf.append("texture($textureUniformId, vec2($uvParamName.x, 1. - $uvParamName.y))")
        }

        override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
            return object : FeedContext, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                    private val textureUnit = gl.getTextureUnit(VideoInPlugin)
                    private val texture = gl.check { createTexture() }

                    override fun bind(glslProgram: GlslProgram): ProgramFeedContext = object : ProgramFeedContext {
                        val textureId = "ds_${getVarName(id)}_texture"
                        val videoUniform = glslProgram.getUniform(textureId)
                        override val isValid: Boolean
                            get() = videoUniform != null

                        override fun setOnProgram() {
                            videoUniform?.let { uniform ->
                                with(textureUnit) {
                                    bindTexture(texture)
                                    configure(GL_LINEAR, GL_LINEAR)
                                    uploadTexture(
                                        0, GL_RGBA, 0,
                                        videoProvider.getTextureResource()
                                    )
                                }

                                uniform.set(textureUnit)
                            }
                        }
                    }

                    override fun release() {
                        gl.check { deleteTexture(texture) }
                        textureUnit.release()
                    }
                }
            }
        }
    }

    companion object : Plugin<Any> {
        override val id: String = "baaahs.VideoIn"

        override fun getArgs(parser: ArgParser): Any = Any()

        override fun openForServer(pluginContext: PluginContext, args: Any): OpenServerPlugin {
            return VideoInPlugin(DefaultVideoProvider)
        }

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin {
            return VideoInPlugin(DefaultVideoProvider)
        }
    }
}
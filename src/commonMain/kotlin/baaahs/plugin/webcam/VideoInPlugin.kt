package baaahs.plugin.webcam

import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ProgramBuilder
import baaahs.gl.shader.InputPort
import baaahs.plugin.*
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import com.danielgergely.kgl.GL_LINEAR
import com.danielgergely.kgl.GL_RGBA
import kotlinx.serialization.SerialName

class VideoInPlugin(private val videoProvider: VideoProvider) : OpenServerPlugin, OpenClientPlugin {
    override val packageName: String = id
    override val title: String = "Video In"

    // We'll just make one up-front. We only ever want one (because equality
    // is using object identity), and there's no overhead.
    internal val videoInFeed = VideoInFeed()

    override val feedBuilders: List<FeedBuilder<VideoInFeed>>
        get() = listOf(
            object : FeedBuilder<VideoInFeed> {
                override val title get() = "Video Input"
                override val description get() = "Video input."
                override val resourceName get() = "VideoIn"
                override val contentType get() = ContentType.Color
                override val serializerRegistrar
                    get() = objectSerializer("$id:$resourceName", videoInFeed)
                override val isFunctionFeed: Boolean = true

                override fun build(inputPort: InputPort) = videoInFeed

                override fun exampleDeclaration(varName: String): String =
                    "vec4 $varName(vec2 uv);"
            }
        )

    @SerialName("baaahs.VideoIn:VideoIn")
    inner class VideoInFeed internal constructor() : Feed {
        override val pluginPackage: String get() = id
        override val title: String get() = "Video In"
        override fun getType(): GlslType = GlslType.Sampler2D
        override val contentType: ContentType get() = ContentType.Color

        override fun appendDeclaration(buf: ProgramBuilder, id: String) {
            val textureUniformId = "ds_${getVarName(id)}_texture"
            /**language=glsl*/
            buf.append("uniform sampler2D $textureUniformId;\n")
        }

        override fun appendInvoke(buf: ProgramBuilder, varName: String, inputPort: InputPort) {
            val fn = inputPort.glslArgSite as GlslCode.GlslFunction

            val textureUniformId = "ds_${getVarName(varName)}_texture"
            val uvParamName = fn.params[0].name
            buf.append("texture($textureUniformId, vec2($uvParamName.x, 1. - $uvParamName.y))")
        }

        override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
            return object : FeedContext, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeedContext {
                    return object : EngineFeedContext {
                        private val texture = gl.check { createTexture() }

                        override fun bind(glslProgram: GlslProgram): ProgramFeedContext = object : ProgramFeedContext {
                            val textureId = "ds_${getVarName(id)}_texture"
                            val videoUniform = glslProgram.getTextureUniform(textureId)

                            override val isValid: Boolean
                                get() {
                                    return videoUniform != null
                                }

                            override fun setOnProgram() {
                                videoUniform?.let { uniform ->
                                    uniform.set(texture)

                                    if (!videoProvider.isReady()) return

                                    with(gl) {
                                        texture.configure(GL_LINEAR, GL_LINEAR)
                                        texture.upload(
                                            0, GL_RGBA, 0,
                                            videoProvider.getTextureResource()
                                        )
                                    }

                                }
                            }
                        }

                        override fun release() {
                            gl.check { deleteTexture(texture) }
                        }
                    }
                }
            }
        }
    }

    companion object : Plugin {
        override val id: String = "baaahs.VideoIn"

        override fun openForServer(pluginContext: PluginContext): OpenServerPlugin {
            return VideoInPlugin(DefaultVideoProvider)
        }

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin {
            return VideoInPlugin(DefaultVideoProvider)
        }
    }
}
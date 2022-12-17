package baaahs.plugin.core.feed

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.data.SingleUniformFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import baaahs.util.makeSafeForGlsl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:Time")
data class TimeFeed(@Transient val `_`: Boolean = true) : Feed {
    companion object : FeedBuilder<TimeFeed> {
        override val title: String get() = "Time"
        override val description: String get() = "The current time."
        override val resourceName: String get() = "Time"
        override val contentType: ContentType get() = ContentType.Time
        override val serializerRegistrar get() = classSerializer(serializer())
        override fun build(inputPort: InputPort): TimeFeed =
            TimeFeed()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Time"
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType
        get() = ContentType.Time

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext =
        object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    val clock = showPlayer.toolchain.plugins.pluginContext.clock
                    return SingleUniformFeedContext(glslProgram, this@TimeFeed, id) { uniform ->
                        val thisTime = clock.now().makeSafeForGlsl()
                        uniform.set(thisTime)
                    }
                }
            }
        }
}
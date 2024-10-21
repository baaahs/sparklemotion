package baaahs.show

import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.PluginRef
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.json.JsonObject

data class UnknownFeed(
    val pluginRef: PluginRef,
    val errorMessage: String,
    override val contentType: ContentType,
    val data: JsonObject
) : Feed {
    override val pluginPackage: String get() = pluginRef.pluginId
    override val title: String get() = "Unknown Feed ${pluginRef.toRef()}"
    override val isUnknown: Boolean = true

    override fun getType(): GlslType = GlslType.Void

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext =
        UnknownFeedContext()

    class UnknownFeedContext : FeedContext, RefCounted by RefCounter() {
        override fun bind(gl: GlContext): EngineFeedContext = UnknownEngineFeedContext()
    }

    class UnknownEngineFeedContext : EngineFeedContext {
        override fun bind(glslProgram: GlslProgram): ProgramFeedContext = UnknownProgramFeedContext()
    }

    class UnknownProgramFeedContext : ProgramFeedContext
}

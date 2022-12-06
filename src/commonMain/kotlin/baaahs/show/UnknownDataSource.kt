package baaahs.show

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.plugin.PluginRef
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.json.JsonObject

data class UnknownDataSource(
    val pluginRef: PluginRef,
    val errorMessage: String,
    override val contentType: ContentType,
    val data: JsonObject
) : DataSource {
    override val pluginPackage: String get() = pluginRef.pluginId
    override val title: String get() = "Unknown DataSource ${pluginRef.toRef()}"
    override val isUnknown: Boolean = true

    override fun getType(): GlslType = GlslType.Void

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext =
        UnknownFeedContext()

    class UnknownFeedContext : FeedContext, RefCounted by RefCounter() {
        override fun bind(gl: GlContext): EngineFeed = UnknownEngineFeed()
    }

    class UnknownEngineFeed : EngineFeed {
        override fun bind(glslProgram: GlslProgram): ProgramFeed = UnknownProgramFeed()
    }

    class UnknownProgramFeed : ProgramFeed
}

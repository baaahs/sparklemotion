package baaahs.plugin.core.datasource

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeed
import baaahs.gl.data.SingleUniformFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Sparkle Motion always uses a resolution of (1, 1), except for previews, which
 * use [PreviewResolutionDataSource] instead.
 */
@Serializable
@SerialName("baaahs.Core:Resolution")
data class ResolutionDataSource(@Transient val `_`: Boolean = true) : DataSource {
    companion object : DataSourceBuilder<ResolutionDataSource> {
        override val title: String get() = "Resolution"
        override val description: String get() = "The resolution of the render viewport, in pixels."
        override val resourceName: String get() = "Resolution"
        override val contentType: ContentType get() = ContentType.Resolution
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean = false
        override fun build(inputPort: InputPort): ResolutionDataSource =
            ResolutionDataSource()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Resolution"
    override fun getType(): GlslType = GlslType.Vec2
    override val contentType: ContentType
        get() = ContentType.Resolution

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext =
        object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed =
                    SingleUniformFeed(glslProgram, this@ResolutionDataSource, id) { uniform ->
                        uniform.set(1f, 1f)
                    }
            }
        }
}
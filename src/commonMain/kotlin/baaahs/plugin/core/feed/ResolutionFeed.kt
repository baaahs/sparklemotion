package baaahs.plugin.core.feed

import baaahs.ShowPlayer
import baaahs.geom.Vector2F
import baaahs.gl.data.singleUniformFeedContext
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Sparkle Motion always uses a resolution of (1, 1), except for previews, which
 * use [PreviewResolutionFeed] instead.
 */
@Serializable
@SerialName("baaahs.Core:Resolution")
data class ResolutionFeed(@Transient val `_`: Boolean = true) : Feed {
    companion object : FeedBuilder<ResolutionFeed> {
        override val title: String get() = "Resolution"
        override val description: String get() = "The resolution of the render viewport, in pixels."
        override val resourceName: String get() = "Resolution"
        override val contentType: ContentType get() = ContentType.Resolution
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean = false
        override fun build(inputPort: InputPort): ResolutionFeed =
            ResolutionFeed()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Resolution"
    override fun getType(): GlslType = GlslType.Vec2
    override val contentType: ContentType
        get() = ContentType.Resolution

    override fun open(showPlayer: ShowPlayer, id: String) =
        singleUniformFeedContext<Vector2F>(id) { Vector2F.unit2d }
}
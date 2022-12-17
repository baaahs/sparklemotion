package baaahs.plugin.core.feed

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Deprecated("Obsolete, going away soon.")
@Serializable
@SerialName("baaahs.Core:PixelCoordsTexture")
data class PixelCoordsTextureFeed(@Transient val `_`: Boolean = true) : Feed {
    companion object : FeedBuilder<PixelCoordsTextureFeed> {
        override val title: String get() = "Pixel Coordinates"
        override val description: String get() = "Internal use only."
        override val resourceName: String get() = "PixelCoords"
        override val contentType: ContentType get() = ContentType.PixelCoordinatesTexture
        override val serializerRegistrar get() = classSerializer(serializer())
        override val internalOnly: Boolean = true

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean = false
        override fun build(inputPort: InputPort): PixelCoordsTextureFeed =
            PixelCoordsTextureFeed()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Pixel Coordinates Texture"
    override fun getType(): GlslType = GlslType.Sampler2D
    override val contentType: ContentType
        get() = ContentType.PixelCoordinatesTexture
    override fun suggestId(): String = "pixelCoordsTexture"

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext =
        object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext = object : ProgramFeedContext {
                    override val isValid: Boolean get() = false
                }
            }
        }
}
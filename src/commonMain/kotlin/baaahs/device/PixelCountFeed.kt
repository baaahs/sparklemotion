package baaahs.device

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderTarget
import baaahs.gl.shader.InputPort
import baaahs.plugin.SerializerRegistrar
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.UpdateMode
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:PixelCount")
data class PixelCountFeed(@Transient val `_`: Boolean = true) : Feed {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Pixel Count"
    override fun getType(): GlslType = GlslType.Int
    override val contentType: ContentType
        get() = ContentType.XyzCoordinate

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
        return PixelCountFeedContext(getVarName(id))
    }

    companion object : FeedBuilder<PixelCountFeed> {
        override val title: String get() = "Pixel Count"
        override val description: String get() = "The number of pixels in this fixture."
        override val resourceName: String
            get() = "PixelCount"
        override val contentType: ContentType
            get() = ContentType.PixelCount
        override val serializerRegistrar: SerializerRegistrar<PixelCountFeed>
            get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean = false
        override fun build(inputPort: InputPort): PixelCountFeed =
            PixelCountFeed()
    }
}

class PixelCountFeedContext(
    private val id: String
) : FeedContext, RefCounted by RefCounter() {

    override fun bind(gl: GlContext) = object : EngineFeedContext {
        override fun bind(glslProgram: GlslProgram) = object : ProgramFeedContext {
            private val uniform = glslProgram.getUniform(id)

            override val updateMode: UpdateMode
                get() = UpdateMode.PER_FIXTURE
            override val isValid: Boolean = uniform != null

            override fun setOnProgram(renderTarget: RenderTarget) {
                uniform?.set(renderTarget.componentCount)
            }
        }
    }
}

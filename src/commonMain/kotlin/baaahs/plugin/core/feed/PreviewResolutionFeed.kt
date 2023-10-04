package baaahs.plugin.core.feed

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
import baaahs.show.FeedOpenContext
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:PreviewResolution")
data class PreviewResolutionFeed(@Transient val `_`: Boolean = true) : Feed {
    companion object : FeedBuilder<PreviewResolutionFeed> {
        override val title: String get() = "Preview Resolution"
        override val description: String get() = "Internal use only."
        override val resourceName: String get() = "PreviewResolution"
        override val contentType: ContentType get() = ContentType.PreviewResolution
        override val serializerRegistrar get() = classSerializer(serializer())
        override val internalOnly: Boolean = true

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean = false
        override fun build(inputPort: InputPort): PreviewResolutionFeed =
            PreviewResolutionFeed()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "PreviewResolution"
    override fun getType(): GlslType = GlslType.Vec2
    override val contentType: ContentType
        get() = ContentType.PreviewResolution

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext =
        object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext =
                    object : ProgramFeedContext, GlslProgram.ResolutionListener {
                        private val uniform = glslProgram.getFloat2Uniform(getVarName(id))
                        override val isValid: Boolean = uniform != null

                        private var x = 1f
                        private var y = 1f

                        override fun onResolution(x: Float, y: Float) {
                            this.x = x
                            this.y = y
                        }

                        override fun setOnProgram() {
                            uniform?.set(x, y)
                        }
                    }
            }
        }
}
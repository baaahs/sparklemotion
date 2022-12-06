package baaahs.plugin.core.datasource

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeed
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

@Serializable
@SerialName("baaahs.Core:PreviewResolution")
data class PreviewResolutionDataSource(@Transient val `_`: Boolean = true) : DataSource {
    companion object : DataSourceBuilder<PreviewResolutionDataSource> {
        override val title: String get() = "Preview Resolution"
        override val description: String get() = "Internal use only."
        override val resourceName: String get() = "PreviewResolution"
        override val contentType: ContentType get() = ContentType.PreviewResolution
        override val serializerRegistrar get() = classSerializer(serializer())
        override val internalOnly: Boolean = true

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean = false
        override fun build(inputPort: InputPort): PreviewResolutionDataSource =
            PreviewResolutionDataSource()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "PreviewResolution"
    override fun getType(): GlslType = GlslType.Vec2
    override val contentType: ContentType
        get() = ContentType.PreviewResolution

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext =
        object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed =
                    object : ProgramFeed, GlslProgram.ResolutionListener {
                        private val uniform = glslProgram.getUniform(getVarName(id))
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
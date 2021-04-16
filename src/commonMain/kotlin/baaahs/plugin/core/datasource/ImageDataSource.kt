package baaahs.plugin.core.datasource

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.camelize
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("baaahs.Core:Image")
data class ImageDataSource(val imageTitle: String) : DataSource {
    companion object : DataSourceBuilder<ImageDataSource> {
        override val resourceName: String get() = "Image"
        override val contentType: ContentType get() = ContentType.Color
        override val serializerRegistrar get() = classSerializer(serializer())
        override fun looksValid(inputPort: InputPort): Boolean =
            inputPort.dataTypeIs(GlslType.Sampler2D)

        override fun build(inputPort: InputPort): ImageDataSource =
            ImageDataSource(inputPort.title)
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Image"
    override fun getType(): GlslType = GlslType.Sampler2D
    override val contentType: ContentType get() = ContentType.Color

    override fun suggestId(): String = "$imageTitle Image".camelize()

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
        object : Feed, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed =
                    SingleUniformFeed(glslProgram, this@ImageDataSource, id) {
                        // no-op
                    }
            }

            override fun release() = Unit
        }
}
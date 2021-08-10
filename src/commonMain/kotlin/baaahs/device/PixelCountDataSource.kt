package baaahs.device

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderTarget
import baaahs.gl.shader.InputPort
import baaahs.plugin.SerializerRegistrar
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.UpdateMode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:PixelCount")
data class PixelCountDataSource(@Transient val `_`: Boolean = true) : DataSource {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Pixel Count"
    override fun getType(): GlslType = GlslType.Int
    override val contentType: ContentType
        get() = ContentType.XyzCoordinate

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        return PixelCountFeed(getVarName(id))
    }

    companion object : DataSourceBuilder<PixelCountDataSource> {
        override val title: String get() = "Pixel Count"
        override val description: String get() = "The number of pixels in this fixture."
        override val resourceName: String
            get() = "PixelCount"
        override val contentType: ContentType
            get() = ContentType.PixelCount
        override val serializerRegistrar: SerializerRegistrar<PixelCountDataSource>
            get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean = false
        override fun build(inputPort: InputPort): PixelCountDataSource =
            PixelCountDataSource()
    }
}

class PixelCountFeed(
    private val id: String,
    private val refCounter: RefCounter = RefCounter()
) : Feed, RefCounted by refCounter {

    override fun bind(gl: GlContext) = object : EngineFeed {
        override fun bind(glslProgram: GlslProgram) = object : ProgramFeed {
            private val uniform = glslProgram.getUniform(id)

            override val updateMode: UpdateMode
                get() = UpdateMode.PER_FIXTURE
            override val isValid: Boolean = uniform != null

            override fun setOnProgram(renderTarget: RenderTarget) {
                uniform?.set(renderTarget.pixelCount)
            }
        }
    }

    override fun release() {
        refCounter.release()
    }
}

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
import baaahs.util.makeSafeForGlsl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:Time")
data class TimeDataSource(@Transient val `_`: Boolean = true) : DataSource {
    companion object : DataSourceBuilder<TimeDataSource> {
        override val title: String get() = "Time"
        override val description: String get() = "The current time."
        override val resourceName: String get() = "Time"
        override val contentType: ContentType get() = ContentType.Time
        override val serializerRegistrar get() = classSerializer(serializer())
        override fun build(inputPort: InputPort): TimeDataSource =
            TimeDataSource()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Time"
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType
        get() = ContentType.Time

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext =
        object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed {
                    val clock = showPlayer.toolchain.plugins.pluginContext.clock
                    return SingleUniformFeed(glslProgram, this@TimeDataSource, id) { uniform ->
                        val thisTime = clock.now().makeSafeForGlsl()
                        uniform.set(thisTime)
                    }
                }
            }
        }
}
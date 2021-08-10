package baaahs.plugin.core.datasource

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
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
import com.soywiz.klock.DateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

@Serializable
@SerialName("baaahs.Core:Date")
data class DateDataSource(
    val zeroBasedMonth: Boolean = false,
    val zeroBasedDay: Boolean = false
) : DataSource {

    companion object : DataSourceBuilder<DateDataSource> {
        const val SECONDS_PER_DAY = (24 * 60 * 60).toDouble()

        override val resourceName: String get() = "Date"
        override val contentType: ContentType get() = ContentType.Date
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == contentType

        override fun build(inputPort: InputPort): DateDataSource =
            DateDataSource(
                inputPort.pluginConfig?.get("zeroBasedMonth") == JsonPrimitive(true),
                inputPort.pluginConfig?.get("zeroBasedDay") == JsonPrimitive(true)
            )
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Date"
    override fun getType(): GlslType = GlslType.Vec4
    override val contentType: ContentType
        get() = ContentType.Date

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
        object : Feed, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed {
                    val clock = showPlayer.toolchain.plugins.pluginContext.clock
                    return SingleUniformFeed(glslProgram, this@DateDataSource, id) { uniform ->
                        val dateTime = DateTime(clock.now() * 1000)
                        uniform.set(
                            dateTime.yearInt.toFloat(),
                            dateTime.month1.toFloat() - if (zeroBasedMonth) 1 else 0,
                            dateTime.dayOfMonth.toFloat() - if (zeroBasedDay) 1 else 0,
                            (dateTime.yearOneMillis / 1000.0 % SECONDS_PER_DAY).toFloat()
                        )
                    }
                }
            }

            override fun release() = Unit
        }
}
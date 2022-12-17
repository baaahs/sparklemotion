package baaahs.plugin.core.datasource

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.data.SingleUniformFeedContext
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

        override val title: String get() = "Date"
        override val description: String get() = "The current date in a `vec4`:\n" +
                "<br/>`x`: year\n" +
                "<br/>`y`: month (January == 1)\n" +
                "<br/>`z`: day of month (First == 1)\n" +
                "<br/>`w`: time of day in milliseconds past midnight"
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

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext =
        object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    val clock = showPlayer.toolchain.plugins.pluginContext.clock
                    return SingleUniformFeedContext(glslProgram, this@DateDataSource, id) { uniform ->
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
        }
}
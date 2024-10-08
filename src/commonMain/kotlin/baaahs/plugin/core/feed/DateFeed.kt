package baaahs.plugin.core.feed

import baaahs.geom.Vector4F
import baaahs.gl.data.FeedContext
import baaahs.gl.data.singleUniformFeedContext
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

@Serializable
@SerialName("baaahs.Core:Date")
data class DateFeed(
    val zeroBasedMonth: Boolean = false,
    val zeroBasedDay: Boolean = false
) : Feed {

    companion object : FeedBuilder<DateFeed> {
        const val SECONDS_PER_DAY = (24 * 60 * 60).toDouble()

        override val title: String get() = "Date"
        override val description: String get() = "The current date in a `vec4`:\n" +
                "`x`: year\n" +
                "`y`: month (January == 1)\n" +
                "`z`: day of month (First == 1)\n" +
                "`w`: time of day in milliseconds past midnight"
        override val resourceName: String get() = "Date"
        override val contentType: ContentType get() = ContentType.Date
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == contentType

        override fun build(inputPort: InputPort): DateFeed =
            DateFeed(
                inputPort.pluginConfig?.get("zeroBasedMonth") == JsonPrimitive(true),
                inputPort.pluginConfig?.get("zeroBasedDay") == JsonPrimitive(true)
            )
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Date"
    override fun getType(): GlslType = GlslType.Vec4
    override val contentType: ContentType
        get() = ContentType.Date

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
        val clock = feedOpenContext.clock
        return singleUniformFeedContext<Vector4F>(id) {
            val tz = clock.tz()
            val dateTime = clock.now().toLocalDateTime(tz)
            Vector4F(
                dateTime.year.toFloat(),
                dateTime.monthNumber.toFloat() - if (zeroBasedMonth) 1 else 0,
                dateTime.dayOfMonth.toFloat() - if (zeroBasedDay) 1 else 0,
                dateTime.time.toMillisecondOfDay() / 1000.0.toFloat()
            )
        }
    }
}
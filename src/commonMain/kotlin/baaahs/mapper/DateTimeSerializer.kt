package baaahs.mapper

import com.soywiz.klock.DateTime
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind.STRING

object DateTimeSerializer : KSerializer<DateTime> {
    override val descriptor = PrimitiveDescriptor("DateTime", STRING)

    override fun deserialize(decoder: Decoder): DateTime {
        return DateTime(decoder.decodeDouble())
    }

    override fun serialize(encoder: Encoder, obj: DateTime) {
        encoder.encodeDouble(obj.unixMillis)
    }
}
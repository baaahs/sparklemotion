package baaahs.mapper

import com.soywiz.klock.DateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object DateTimeSerializer : KSerializer<DateTime> {
    override val descriptor = PrimitiveSerialDescriptor("DateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): DateTime {
        return DateTime(decoder.decodeDouble())
    }

    override fun serialize(encoder: Encoder, obj: DateTime) {
        encoder.encodeDouble(obj.unixMillis)
    }
}
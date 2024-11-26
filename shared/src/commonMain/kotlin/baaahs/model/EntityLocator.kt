@file:OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)

package baaahs.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable(with = EntityLocatorSerializer::class)
data class EntityLocator(
    val id: String = Uuid.random().toHexString()
) {
    fun append(suffix: String) = EntityLocator("$id-$suffix")

    companion object {
        fun next() = EntityLocator()
    }
}

object EntityLocatorSerializer : KSerializer<EntityLocator> {
    override val descriptor: SerialDescriptor
        get() = String.serializer().descriptor

    override fun serialize(encoder: Encoder, value: EntityLocator) {
        String.serializer().serialize(encoder, value.id)
    }

    override fun deserialize(decoder: Decoder): EntityLocator {
        return EntityLocator(String.serializer().deserialize(decoder))
    }
}
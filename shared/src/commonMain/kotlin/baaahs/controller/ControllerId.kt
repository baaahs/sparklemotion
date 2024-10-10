package baaahs.controller

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ControllerId.Serializer::class)
data class ControllerId(val controllerType: String, val id: String) : Comparable<ControllerId> {
    fun name(): String = "$controllerType:$id"

    override fun compareTo(other: ControllerId): Int = comparator.compare(this, other)

    object Serializer : KSerializer<ControllerId> {
        override val descriptor: SerialDescriptor
            get() = String.serializer().descriptor

        override fun serialize(encoder: Encoder, value: ControllerId) {
            String.serializer().serialize(encoder, value.name())
        }

        override fun deserialize(decoder: Decoder): ControllerId {
            return fromName(String.serializer().deserialize(decoder))
        }
    }

    companion object {
        fun fromName(name: String) =
            name.split(":", limit = 2).let {
                if (it.size != 2) error("Can't create ControllerId from $name.")
                ControllerId(it[0], it[1])
            }

        val comparator = compareBy<ControllerId> { it.controllerType }.thenBy { it.id }
    }
}
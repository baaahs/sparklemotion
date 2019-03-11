package baaahs

interface Ports {
    companion object {
        val MAPPER = 8001
        val PINKY = 8002
        val BRAIN = 8003
    }
}

enum class Type {
    BRAIN_HELLO,
    MAPPER_HELLO,
    PINKY_PONG
}

fun parse(bytes: ByteArray): Message {
    val reader = ByteArrayReader(bytes)
    return when (Type.values()[reader.readByte().toInt()]) {
        Type.BRAIN_HELLO -> BrainHelloMessage()
        Type.MAPPER_HELLO -> MapperHelloMessage()
        Type.PINKY_PONG -> PinkyPongMessage.parse(bytes)
    }
}

class BrainHelloMessage : Message(Type.BRAIN_HELLO)
class MapperHelloMessage : Message(Type.MAPPER_HELLO)
class PinkyPongMessage(val brainIds: List<String>) : Message(Type.PINKY_PONG) {
    companion object {
        fun parse(bytes: ByteArray): PinkyPongMessage {
            val reader = ByteArrayReader(bytes, 1)
            val brainCount = reader.readInt();
            val brainIds = mutableListOf<String>()
            for (i in 0..brainCount) {
                brainIds.add(reader.readString())
            }
            return PinkyPongMessage(brainIds)
        }
    }

    override fun toBytes(): ByteArray {
        val writer = ByteArrayWriter()
        writer.writeByte(type.ordinal.toByte())
        writer.writeInt(brainIds.size)
        brainIds.forEach { writer.writeString(it) }
        return writer.toBytes()
    }
}

open class Message(val type: Type) {
    open fun toBytes(): ByteArray {
        val byteArray = ByteArray(1 + size())
        byteArray[0] = type.ordinal.toByte()
        return byteArray
    }

    private fun size(): Int = 0
}
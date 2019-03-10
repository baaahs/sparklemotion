package baaahs

interface Ports {
    companion object {
        val MAPPER = 8001
        val CENTRAL = 8002
        val CONTROLLER = 8003
    }
}

enum class Type {
    CONTROLLER_HELLO,
    MAPPER_HELLO,
    CENTRAL_PONG
}

fun parse(bytes: ByteArray): Message {
    val reader = ByteArrayReader(bytes)
    return when (Type.values()[reader.readByte().toInt()]) {
        Type.CONTROLLER_HELLO -> ControllerHelloMessage()
        Type.MAPPER_HELLO -> MapperHelloMessage()
        Type.CENTRAL_PONG -> CentralPongMessage.parse(bytes)
    }
}

class ControllerHelloMessage : Message(Type.CONTROLLER_HELLO)
class MapperHelloMessage : Message(Type.MAPPER_HELLO)
class CentralPongMessage(val controllerIds: List<String>) : Message(Type.CENTRAL_PONG) {
    companion object {
        fun parse(bytes: ByteArray): CentralPongMessage {
            val reader = ByteArrayReader(bytes, 1)
            val controllerCount = reader.readInt();
            val controllerIds = mutableListOf<String>()
            for (i in 0..controllerCount) {
                controllerIds.add(reader.readString())
            }
            return CentralPongMessage(controllerIds)
        }
    }

    override fun toBytes(): ByteArray {
        val writer = ByteArrayWriter()
        writer.writeByte(type.ordinal.toByte())
        writer.writeInt(controllerIds.size)
        controllerIds.forEach { writer.writeString(it) }
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
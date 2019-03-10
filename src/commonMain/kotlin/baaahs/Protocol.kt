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
            for (i in (0..controllerCount)) {
                controllerIds.add(reader.readString())
            }
            return CentralPongMessage(controllerIds)
        }
    }
}

private class ProtoBuf(val type: Type) {
    fun toBytes(): ByteArray {
        val byteArray = ByteArray(4)
        byteArray[0] = type.ordinal.toByte()
        return byteArray
    }
}

open class Message(val type: Type) {
    fun toBytes(): ByteArray {
        val byteArray = ByteArray(1 + size())
        byteArray[0] = type.ordinal.toByte()
        return byteArray
    }

    private fun size(): Int = 0
}

class ByteArrayWriter(bytes: ByteArray = ByteArray(128), var offset: Int = 0) {
    constructor(size: Int) : this(ByteArray(size))

    private var bytes = bytes

    fun writeByte(b: Byte) {
        growIfNecessary(1)
        bytes[offset++] = b
    }

    fun writeShort(s: Short) {
        growIfNecessary(2)
        bytes[offset++] = s.toInt().shr(8).and(0xff).toByte()
        bytes[offset++] = s.toInt().and(0xff).toByte()
    }

    fun writeChar(c: Char) = writeShort(c.toShort())

    fun writeInt(l: Int) {
        growIfNecessary(4)
        bytes[offset++] = l.toInt().shr(24).and(0xff).toByte()
        bytes[offset++] = l.toInt().shr(16).and(0xff).toByte()
        bytes[offset++] = l.toInt().shr(8).and(0xff).toByte()
        bytes[offset++] = l.toInt().and(0xff).toByte()
    }

    fun writeString(s: String) {
        growIfNecessary(4 + 2 * s.length)
        writeInt(s.length)
        for (i in 0..s.length) {
            writeChar(s[i])
        }
    }

    private fun growIfNecessary(by: Int) {
        if (bytes.size - offset > by) {
            bytes.copyOf(bytes.size * 2)
        }
    }
}

class ByteArrayReader(val bytes: ByteArray, var offset: Int = 0) {
    fun readByte(): Byte = bytes[offset++]

    fun readShort(): Short =
        (bytes[offset++].toInt() shl 8)
            .or(bytes[offset++].toInt()).toShort()

    fun readChar(): Char = readShort().toChar()

    fun readInt(): Int =
        (bytes[offset++].toInt() shl 24)
            .or(bytes[offset++].toInt() shl 16)
            .or(bytes[offset++].toInt() shl 8)
            .or(bytes[offset++].toInt())

    fun readString(): String {
        var length = readInt()
        val buf = StringBuilder(length)
        for (i in 0..length) {
            buf.append(readChar())
        }
        return buf.toString()
    }
}
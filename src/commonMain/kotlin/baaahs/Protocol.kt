package baaahs

enum class Type {
    HELLO
}

public fun parse(bytes: ByteArray): Message {
    return when (Type.values()[bytes[0].toInt()]) {
        Type.HELLO -> HelloMessage()
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
        byteArray[0] = Type.HELLO.ordinal.toByte()
        return byteArray
    }

    private fun size(): Int = 0
}

class HelloMessage : Message(Type.HELLO)

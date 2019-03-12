package baaahs

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
        bytes[offset++] = l.shr(24).and(0xff).toByte()
        bytes[offset++] = l.shr(16).and(0xff).toByte()
        bytes[offset++] = l.shr(8).and(0xff).toByte()
        bytes[offset++] = l.and(0xff).toByte()
    }

    fun writeString(s: String) {
        growIfNecessary(4 + 2 * s.length)
        writeInt(s.length)
        for (i in s.indices) {
            writeChar(s[i])
        }
    }

    fun toBytes(): ByteArray {
        return bytes.copyOf(offset)
    }

    private fun growIfNecessary(by: Int) {
        if (offset + by >= bytes.size) {
            bytes = bytes.copyOf(bytes.size * 2)
        }
    }
}

class ByteArrayReader(val bytes: ByteArray, var offset: Int = 0) {
    fun readByte(): Byte = bytes[offset++]

    fun readShort(): Short =
        (bytes[offset++].toInt() and 0xff shl 8)
            .or(bytes[offset++].toInt() and 0xff).toShort()

    fun readChar(): Char = readShort().toChar()

    fun readInt(): Int =
        (bytes[offset++].toInt() and 0xff shl 24)
            .or(bytes[offset++].toInt() and 0xff shl 16)
            .or(bytes[offset++].toInt() and 0xff shl 8)
            .or(bytes[offset++].toInt() and 0xff)

    fun readString(): String {
        var length = readInt()
        val buf = StringBuilder(length)
        for (i in 0 until length) {
            buf.append(readChar())
        }
        return buf.toString()
    }
}
package baaahs.io

class ByteArrayReader(val bytes: ByteArray, offset: Int = 0) {
    var offset = offset
        set(value) {
            if (value > bytes.size) {
                throw IllegalStateException("array index out of bounds")
            }
            field = value
        }
    fun readBoolean(): Boolean = bytes[offset] != 0.toByte()

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

    fun readFloat(): Float = Float.fromBits(readInt())

    fun readString(): String {
        val length = readInt()
        val buf = StringBuilder(length)
        for (i in 0 until length) {
            buf.append(readChar())
        }
        return buf.toString()
    }

    fun readBytes(): ByteArray {
        val count = readInt()
        return readNBytes(count)
    }

    fun readNBytes(count: Int): ByteArray {
        val bytes = bytes.copyOfRange(offset, offset + count)
        offset += count
        return bytes
    }
}
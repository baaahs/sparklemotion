package baaahs.io

import kotlin.math.min

class ByteArrayReader(val bytes: ByteArray, offset: Int = 0) {
    var offset = offset
        set(value) {
            if (value > bytes.size) {
                throw IllegalStateException("array index out of bounds")
            }
            field = value
        }

    fun readBoolean(): Boolean = bytes[offset++].toInt() != 0

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

    fun readLong(): Long =
        (readInt().toLong() and 0xffffffff shl 32)
            .or(readInt().toLong() and 0xffffffff)

    fun readFloat(): Float = Float.fromBits(readInt())

    @UseExperimental(ExperimentalStdlibApi::class)
    fun readString(): String = readBytes().decodeToString()

    fun readNullableString(): String? = if (readBoolean()) readString() else null

    fun readBytes(): ByteArray {
        val count = readInt()
        return readNBytes(count)
    }

    fun readNBytes(count: Int): ByteArray {
        val bytes = bytes.copyOfRange(offset, offset + count)
        offset += count
        return bytes
    }

    fun readNBytes(dest: ByteArray): ByteArray {
        val bytes = bytes.copyInto(dest, offset, offset + dest.size)
        offset += dest.size
        return bytes
    }

    /**
     * Reads up to as many bytes as are present in `buffer`, or as many bytes are available in the incoming byte array,
     * and returns the number of bytes actually read. Any unread incoming bytes are skipped.
     */
    fun readBytes(buffer: ByteArray): Int {
        val count = readInt()
        val toCopy = min(buffer.size, count)
        bytes.copyInto(buffer, 0, offset, offset + toCopy)
        offset += count
        return toCopy
    }

    fun hasMoreBytes(): Boolean = offset < bytes.size
}
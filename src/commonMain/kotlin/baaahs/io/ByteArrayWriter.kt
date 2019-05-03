package baaahs.io

class ByteArrayWriter(private var bytes: ByteArray = ByteArray(128), var offset: Int = 0) {
    constructor(size: Int) : this(ByteArray(size))

    fun writeBoolean(b: Boolean) {
        growIfNecessary(1)
        bytes[offset++] = if (b) 1 else 0
    }

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

    fun writeFloat(f: Float) {
        writeInt(f.toBits())
    }

    fun writeString(s: String) {
        growIfNecessary(4 + 2 * s.length)
        writeInt(s.length)
        for (i in s.indices) {
            writeChar(s[i])
        }
    }

    fun writeBytes(data: ByteArray, startIndex: Int = 0, endIndex: Int = data.size) {
        val size = endIndex - startIndex

        growIfNecessary(4 + size)
        writeInt(size)

        data.copyInto(bytes, offset, startIndex, endIndex)
        offset += size
    }

    fun writeNBytes(data: ByteArray, startIndex: Int = 0, endIndex: Int = data.size) {
        val size = endIndex - startIndex

        growIfNecessary(size)

        data.copyInto(bytes, offset, startIndex, endIndex)
        offset += size
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
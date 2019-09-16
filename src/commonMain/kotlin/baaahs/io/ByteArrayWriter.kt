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

    fun writeShort(i: Int) {
        if (i and 0xffff != i) {
            throw IllegalArgumentException("$i doesn't fit in a short")
        }
        writeShort(i.toShort())
    }

    fun writeShort(s: Short) {
        growIfNecessary(2)
        bytes[offset++] = s.toInt().shr(8).and(0xff).toByte()
        bytes[offset++] = s.toInt().and(0xff).toByte()
    }

    fun writeChar(c: Char) = writeShort(c.toShort())

    fun writeInt(i: Int) {
        growIfNecessary(4)
        bytes[offset++] = i.shr(24).and(0xff).toByte()
        bytes[offset++] = i.shr(16).and(0xff).toByte()
        bytes[offset++] = i.shr(8).and(0xff).toByte()
        bytes[offset++] = i.and(0xff).toByte()
    }

    fun writeLong(l: Long) {
        growIfNecessary(8)
        writeInt(l.shr(32).and(0xffffffff).toInt())
        writeInt(l.and(0xffffffff).toInt())
    }

    fun writeFloat(f: Float) {
        writeInt(f.toBits())
    }

    fun writeString(s: String) {
        writeBytes(s.encodeToByteArray())
    }

    fun writeNullableString(s: String?) {
        writeBoolean(s != null)
        if (s != null) {
            writeString(s)
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
        if (offset + by > bytes.size) {
            var newSize = bytes.size * 2
            while (offset + by > newSize) newSize *= 2
            bytes = bytes.copyOf(newSize)
        }
    }
}
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

    fun writeUByte(b: UByte) {
        writeByte(b.toByte())
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

    fun writeUInt(i: UInt) {
        growIfNecessary(4)
        bytes[offset++] = i.shr(24).and(0xffu).toByte()
        bytes[offset++] = i.shr(16).and(0xffu).toByte()
        bytes[offset++] = i.shr(8).and(0xffu).toByte()
        bytes[offset++] = i.and(0xffu).toByte()
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
        writeBytesWithSize(s.encodeToByteArray())
    }

    fun writeNullableString(s: String?) {
        writeBoolean(s != null)
        if (s != null) {
            writeString(s)
        }
    }

    fun writeBytes(vararg bytes: Int) {
        for (byte in bytes) {
            val b = byte.toByte()
            if (b.toInt() != byte) {
                throw IllegalArgumentException("$byte doesn't fit in a byte")
            }

            writeByte(b)
        }
    }

    fun writeBytes(vararg bytes: Byte) {
        for (byte in bytes) {
            writeByte(byte)
        }
    }

    fun writeBytes(data: ByteArray, startIndex: Int = 0, endIndex: Int = data.size) {
        val size = endIndex - startIndex

        growIfNecessary(size)

        data.copyInto(bytes, offset, startIndex, endIndex)
        offset += size
    }

    fun writeBytesWithSize(data: ByteArray, startIndex: Int = 0, endIndex: Int = data.size) {
        val size = endIndex - startIndex

        growIfNecessary(4 + size)
        writeInt(size)

        data.copyInto(bytes, offset, startIndex, endIndex)
        offset += size
    }

    fun toBytes(): ByteArray {
        return if (bytes.size == offset)
            bytes
        else
            bytes.copyOf(offset)
    }

    fun copyBytes(): ByteArray =
        bytes.copyOf(offset)

    fun at(offset: Int): ByteArrayWriter = ByteArrayWriter(bytes, offset)

    fun reset() {
        offset = 0
    }

    private fun growIfNecessary(by: Int) {
        if (offset + by > bytes.size) {
            var newSize = bytes.size * 2
            while (offset + by > newSize) newSize *= 2
            bytes = bytes.copyOf(newSize)
        }
    }
}
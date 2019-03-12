package baaahs

import kotlin.random.Random

data class Color(val red: Int, val green: Int, val blue: Int) {
    fun serialize(writer: ByteArrayWriter) {
        writer.writeByte((red and 0xff).toByte())
        writer.writeByte((green and 0xff).toByte())
        writer.writeByte((blue and 0xff).toByte())
    }

    fun toInt(): Int =
        (red shl 16 and 0xff0000)
            .or(green shl 8 and 0xff00)
            .or(blue and 0xff)

    companion object {
        val BLACK = Color(0, 0, 0)
        val WHITE = Color(-128, -128, -128)

        fun random() = Color(
            Random.nextInt() and 0xff,
            Random.nextInt() and 0xff,
            Random.nextInt() and 0xff
        )

        fun parse(reader: ByteArrayReader) = Color(
            reader.readByte().toInt() and 0xff,
            reader.readByte().toInt() and 0xff,
            reader.readByte().toInt() and 0xff
        )
    }
}
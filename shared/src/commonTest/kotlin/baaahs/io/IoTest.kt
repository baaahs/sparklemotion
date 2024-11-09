package baaahs.io

import io.kotest.matchers.*
import kotlin.test.Test

class IoTest {
    @Test
    fun testByteArrays() {
        val writer = ByteArrayWriter(bytes = ByteArray(2))
        writer.writeBytesWithSize(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))

        val reader = ByteArrayReader(writer.toBytes())
        reader.readBytesWithSize().toList()
            .shouldBe(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8).toList())
    }

    @Test
    fun testPartialByteArrays() {
        val writer = ByteArrayWriter(bytes = ByteArray(2))
        writer.writeBytesWithSize(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8), 6)

        val reader = ByteArrayReader(writer.toBytes())
        reader.readBytesWithSize().toList()
            .shouldBe(byteArrayOf(7, 8).toList())
    }
}
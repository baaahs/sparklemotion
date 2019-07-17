package baaahs.io

import kotlin.test.Test
import kotlin.test.expect

class IoTest {
    @Test
    fun testByteArrays() {
        val writer = ByteArrayWriter(bytes = ByteArray(2))
        writer.writeBytes(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))

        val reader = ByteArrayReader(writer.toBytes())
        expect(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8).toList()) { reader.readBytes().toList() }
    }

    @Test
    fun testPartialByteArrays() {
        val writer = ByteArrayWriter(bytes = ByteArray(2))
        writer.writeBytes(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8), 6)

        val reader = ByteArrayReader(writer.toBytes())
        expect(byteArrayOf(7, 8).toList()) { reader.readBytes().toList() }
    }
}
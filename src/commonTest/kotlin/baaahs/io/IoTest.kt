package baaahs.io

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test

class IoTest {
    @Test
    fun testByteArrays() {
        val writer = ByteArrayWriter(bytes = ByteArray(2))
        writer.writeBytes(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))

        val reader = ByteArrayReader(writer.toBytes())
        expect(reader.readBytes().toList())
            .toBe(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8).toList())
    }

    @Test
    fun testPartialByteArrays() {
        val writer = ByteArrayWriter(bytes = ByteArray(2))
        writer.writeBytes(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8), 6)

        val reader = ByteArrayReader(writer.toBytes())
        expect(reader.readBytes().toList())
            .toBe(byteArrayOf(7, 8).toList())
    }
}
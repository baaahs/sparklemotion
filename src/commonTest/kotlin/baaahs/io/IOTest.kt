package baaahs.io

import kotlin.test.Test
import kotlin.test.expect

class IOTest {
    @Test
    fun stringEncoding() {
        val abcBuffer = ByteArrayWriter().apply { writeString("abc∂") }.toBytes()

        expect(
            listOf(
                0, 0, 0, 4,
                0, 'a'.toByte(), 0, 'b'.toByte(), 0, 'c'.toByte(), 34, 2
            )
        ) { abcBuffer.toList() }
    }

    @Test
    fun stringRoundTrip() {
        val abcBuffer = ByteArrayWriter().apply { writeString("abc∂") }.toBytes()
        expect("abc∂") { ByteArrayReader(abcBuffer).readString() }
    }
}
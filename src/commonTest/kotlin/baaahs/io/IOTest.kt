package baaahs.io

import kotlin.test.Test
import kotlin.test.expect

class IOTest {
    @Test
    fun stringEncoding() {
        val abcBuffer = ByteArrayWriter().apply { writeString("abc∂") }.toBytes()

        expect(
            listOf(
                0, 0, 0, 6,
                'a'.toByte(), 'b'.toByte(), 'c'.toByte(), -30, -120, -126
            )
        ) { abcBuffer.toList() }
    }

    @Test
    fun stringRoundTrip() {
        val abcBuffer = ByteArrayWriter().apply { writeString("abc∂") }.toBytes()
        expect("abc∂") { ByteArrayReader(abcBuffer).readString() }
    }
}
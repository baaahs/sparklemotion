package baaahs.glsl

import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.test.Test
import kotlin.test.expect

class UvTranslatorTest {
    @Test
    fun testSerialization() {
        val uvTranslator = LinearModelSpaceUvTranslator(Vector3F(1f, 2f, 3f),
            Vector3F(0f, 0f, 0f) to Vector3F(9f, 9f, 9f)
        )

        val destUvTranslator = transmit(uvTranslator)
        expect(uvTranslator.modelCenter) { destUvTranslator.modelCenter }
        expect(uvTranslator.modelBounds) { destUvTranslator.modelBounds }
    }

    private fun <T : UvTranslator> transmit(srcUvTranslator: T): T {
        val writer = ByteArrayWriter()
        srcUvTranslator.serialize(writer)
        val bytes = writer.toBytes()

        expect(srcUvTranslator.id.ordinal.toByte()) { bytes[0] }

        val reader = ByteArrayReader(bytes)
        @Suppress("UNCHECKED_CAST")
        return UvTranslator.parse(reader) as T
    }

}
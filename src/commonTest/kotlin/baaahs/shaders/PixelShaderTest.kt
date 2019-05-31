package baaahs.shaders

import baaahs.Color
import baaahs.Shader
import baaahs.Surface
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.test.Test
import kotlin.test.expect

class PixelShaderTest {
    private val srcShader = PixelShader()
    private val srcBuf = srcShader.Buffer(5).apply {
        (0 until 5).forEach { i -> colors[i] = Color.from(i) }
    }

    @Test
    fun shouldRender() {
        val dstBuf = transmit(srcShader, srcBuf, FakeSurface(5))
        expect("0,1,2,3,4") {
            dstBuf.colors.map { "${it.argb}" }.joinToString(",")
        }
    }

    @Test
    fun whenFewerPixels_shouldTruncate() {
        val dstBuf = transmit(srcShader, srcBuf, FakeSurface(3))
        expect("0,1,2") {
            dstBuf.colors.map { "${it.argb}" }.joinToString(",")
        }
    }

    @Test
    fun whenMorePixels_shouldRepeat() {
        val dstBuf = transmit(srcShader, srcBuf, FakeSurface(12))
        expect("0,1,2,3,4,0,1,2,3,4,0,1") {
            dstBuf.colors.map { "${it.argb}" }.joinToString(",")
        }
    }

    private fun <T : Shader.Buffer> transmit(srcShader: Shader<T>, srcBuf: T, surface: Surface): T {
        val writer = ByteArrayWriter()
        srcShader.serialize(writer)
        srcBuf.serialize(writer)
        val bytes = writer.toBytes()

        val reader = ByteArrayReader(bytes)
        expect(srcShader.id.ordinal.toByte()) { reader.readByte() }

        val dstShader: Shader<T> = srcShader.id.reader.parse(reader) as Shader<T>
        val dstBuf = dstShader.createBuffer(surface)
        dstBuf.read(reader)
        return dstBuf
    }

    class FakeSurface(override val pixelCount: Int) : Surface
}
package baaahs.shaders

import baaahs.Color
import baaahs.Shader
import baaahs.Surface
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.test.Test
import kotlin.test.expect

class PixelShaderTest {
    @Test
    fun shouldRender() {
        val srcShader = PixelShader()
        val srcBuf = srcShader.Buffer(10)

        (0 until 10).forEach { i -> srcBuf.colors[i] = Color.from(i) }
        val dstBuf = transmit(srcShader, srcBuf)
        expect("0,1,2,3,4,5,6,7,8,9") {
            dstBuf.colors.map { "${it.argb}" }.joinToString(",")
        }
    }

    private fun <T : Shader.Buffer> transmit(srcShader: Shader<T>, srcBuf: T): T {
        val writer = ByteArrayWriter()
        srcShader.serialize(writer)
        srcBuf.serialize(writer)
        val bytes = writer.toBytes()

        val reader = ByteArrayReader(bytes)
        expect(srcShader.id.ordinal.toByte()) { reader.readByte() }

        val dstShader: Shader<T> = srcShader.id.reader.parse(reader) as Shader<T>
        val dstBuf = dstShader.createBuffer(FakeSurface(10))
        dstBuf.read(reader)
        return dstBuf
    }

    class FakeSurface(override val pixelCount: Int) : Surface {
        override fun describe(): String = "fake"
    }
}
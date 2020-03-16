package baaahs.shaders

import baaahs.Color
import baaahs.Surface
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.proto.BrainShader
import kotlin.test.expect

private fun <T : BrainShader.Buffer> send(srcShader: BrainShader<T>, srcBuf: T, surface: Surface): Pair<BrainShader<T>, T> {
    val writer = ByteArrayWriter()
    srcShader.serialize(writer)
    srcBuf.serialize(writer)
    val bytes = writer.toBytes()

    val reader = ByteArrayReader(bytes)
    expect(srcShader.id.ordinal.toByte()) { reader.readByte() }

    @Suppress("UNCHECKED_CAST")
    val dstShader: BrainShader<T> = srcShader.id.reader.parse(reader) as BrainShader<T>
    val dstBuf = dstShader.createBuffer(surface)
    dstBuf.read(reader)
    return Pair(dstShader, dstBuf)
}

@Suppress("UNCHECKED_CAST")
internal fun <T : BrainShader.Buffer> transmit(srcBuf: T, surface: Surface): T {
    val (_: BrainShader<T>, dstBuf) = send(srcBuf.shader as BrainShader<T>, srcBuf, surface)
    return dstBuf
}

@Suppress("UNCHECKED_CAST")
internal fun <T : BrainShader.Buffer> render(srcBuf: T, surface: Surface): Pixels {
    val (dstShader: BrainShader<T>, dstBuf) = send(srcBuf.shader as BrainShader<T>, srcBuf, surface)
    val pixels = FakePixels(surface.pixelCount)
    val renderer = dstShader.createRenderer(surface)
    renderer.beginFrame(dstBuf, pixels.size)
    for (i in pixels.indices) {
        pixels[i] = renderer.draw(dstBuf, i)
    }
    renderer.endFrame()
    return pixels
}

internal fun <T : BrainShader.Buffer> render(srcShaderAndBuffer: Pair<BrainShader<T>, T>, surface: Surface): Pixels =
    render(srcShaderAndBuffer.second, surface)

class FakeSurface(override val pixelCount: Int) : Surface {
    override fun describe(): String = "fake"
}

class FakePixels(override val size: Int) : Pixels {
    private val buf = Array(size) { Color.BLACK }

    override fun get(i: Int): Color = buf[i]

    override fun set(i: Int, color: Color) {
        buf[i] = color
    }

    override fun set(colors: Array<Color>) {
        colors.copyInto(buf)
    }
}
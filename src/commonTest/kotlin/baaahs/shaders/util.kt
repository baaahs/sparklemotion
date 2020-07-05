package baaahs.shaders

import baaahs.BrainShader
import baaahs.Color
import baaahs.Pixels
import baaahs.Surface
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.test.expect

private fun <T : BrainShader.Buffer> send(srcBrainShader: BrainShader<T>, srcBuf: T, surface: Surface): Pair<BrainShader<T>, T> {
    val writer = ByteArrayWriter()
    srcBrainShader.serialize(writer)
    srcBuf.serialize(writer)
    val bytes = writer.toBytes()

    val reader = ByteArrayReader(bytes)
    expect(srcBrainShader.idBrain.ordinal.toByte()) { reader.readByte() }

    @Suppress("UNCHECKED_CAST")
    val dstBrainShader: BrainShader<T> = srcBrainShader.idBrain.reader.parse(reader) as BrainShader<T>
    val dstBuf = dstBrainShader.createBuffer(surface)
    dstBuf.read(reader)
    return Pair(dstBrainShader, dstBuf)
}

@Suppress("UNCHECKED_CAST")
internal fun <T : BrainShader.Buffer> transmit(srcBuf: T, surface: Surface): T {
    val (_: BrainShader<T>, dstBuf) = send(srcBuf.brainShader as BrainShader<T>, srcBuf, surface)
    return dstBuf
}

@Suppress("UNCHECKED_CAST")
internal fun <T : BrainShader.Buffer> render(srcBuf: T, surface: Surface): Pixels {
    val (dstBrainShader: BrainShader<T>, dstBuf) = send(srcBuf.brainShader as BrainShader<T>, srcBuf, surface)
    val pixels = FakePixels(surface.pixelCount)
    val renderer = dstBrainShader.createRenderer(surface)
    renderer.beginFrame(dstBuf, pixels.size)
    for (i in pixels.indices) {
        pixels[i] = renderer.draw(dstBuf, i)
    }
    renderer.endFrame()
    return pixels
}

internal fun <T : BrainShader.Buffer> render(srcBrainShaderAndBuffer: Pair<BrainShader<T>, T>, surface: Surface): Pixels =
    render(srcBrainShaderAndBuffer.second, surface)

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
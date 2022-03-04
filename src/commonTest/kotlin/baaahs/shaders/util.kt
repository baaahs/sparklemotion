package baaahs.shaders

import baaahs.Color
import baaahs.TestModel
import baaahs.device.FixtureType
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.fixtures.NullTransport
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.sm.brain.proto.BrainShader
import baaahs.sm.brain.proto.Pixels
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect

private fun <T : BrainShader.Buffer> send(srcBrainShader: BrainShader<T>, srcBuf: T, fixture: Fixture): Pair<BrainShader<T>, T> {
    val writer = ByteArrayWriter()
    srcBrainShader.serialize(writer)
    srcBuf.serialize(writer)
    val bytes = writer.toBytes()

    val reader = ByteArrayReader(bytes)
    expect(reader.readByte()).toBe(srcBrainShader.idBrain.ordinal.toByte())

    @Suppress("UNCHECKED_CAST")
    val dstBrainShader: BrainShader<T> = srcBrainShader.idBrain.reader.parse(reader) as BrainShader<T>
    val dstBuf = dstBrainShader.createBuffer(fixture.pixelCount)
    dstBuf.read(reader)
    return Pair(dstBrainShader, dstBuf)
}

@Suppress("UNCHECKED_CAST")
internal fun <T : BrainShader.Buffer> transmit(srcBuf: T, fixture: Fixture): T {
    val (_: BrainShader<T>, dstBuf) = send(srcBuf.brainShader as BrainShader<T>, srcBuf, fixture)
    return dstBuf
}

@Suppress("UNCHECKED_CAST")
internal fun <T : BrainShader.Buffer> render(srcBuf: T, fixture: Fixture): Pixels {
    val (dstBrainShader: BrainShader<T>, dstBuf) = send(srcBuf.brainShader as BrainShader<T>, srcBuf, fixture)
    val pixels = FakePixels(fixture.pixelCount)
    val renderer = dstBrainShader.createRenderer()
    renderer.beginFrame(dstBuf, pixels.size)
    for (i in pixels.indices) {
        pixels[i] = renderer.draw(dstBuf, i) ?: Color.BLACK
    }
    renderer.endFrame()
    return pixels
}

internal fun <T : BrainShader.Buffer> render(srcBrainShaderAndBuffer: Pair<BrainShader<T>, T>, fixture: Fixture): Pixels =
    render(srcBrainShaderAndBuffer.second, fixture)

fun fakeFixture(
    pixelCount: Int,
    modelEntity: Model.Entity? = null,
    fixtureType: FixtureType = modelEntity?.fixtureType ?: PixelArrayDevice,
    model: Model = TestModel
) =
    fixtureType.createFixture(
        modelEntity, pixelCount, fixtureType.defaultConfig,
        "Fake ${fixtureType.title}", transport = NullTransport, model
    )

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
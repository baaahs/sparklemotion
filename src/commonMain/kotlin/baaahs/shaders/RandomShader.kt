package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.random.Random

/**
 * A shader that sets pixels to a random colors.
 */
class RandomShader : Shader<RandomShader.Buffer>(ShaderId.RANDOM) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createRenderer(surface: Surface): Renderer = Renderer()

    companion object : ShaderReader<RandomShader> {
        override fun parse(reader: ByteArrayReader) = RandomShader()
    }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@RandomShader

        override fun serialize(writer: ByteArrayWriter) {
        }

        override fun read(reader: ByteArrayReader) {
        }
    }

    class Renderer : Shader.Renderer<Buffer> {
        override fun draw(buffer: Buffer, pixelIndex: Int): Color =
            Color.from(Random.nextInt(0xffffff) or (0xff ushr 24))
    }
}

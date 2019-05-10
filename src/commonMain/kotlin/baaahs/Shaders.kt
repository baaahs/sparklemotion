package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.shaders.*

enum class ShaderId(val parser: (reader: ByteArrayReader) -> Shader<*>) {
    SOLID({ reader -> SolidShader.parse(reader) }),
    PIXEL({ reader -> PixelShader.parse(reader) }),
    SINE_WAVE({ reader -> SineWaveShader.parse(reader) }),
    COMPOSITOR({ reader -> CompositorShader.parse(reader) }),
    SPARKLE({ reader -> SparkleShader.parse(reader) });

    companion object {
        val values = values()
        fun get(i: Byte): ShaderId {
            if (i > values.size || i < 0) {
                throw Throwable("bad index for ShaderId: ${i}")
            }
            return values[i.toInt()]
        }
    }
}

interface Surface {
    val pixelCount: Int
}

abstract class Shader<B : ShaderBuffer>(val id: ShaderId) {
    abstract fun createImpl(pixels: Pixels): ShaderImpl<B>

    abstract fun createBuffer(surface: Surface): B

    val descriptorBytes: ByteArray by lazy { toBytes() }

    fun serialize(writer: ByteArrayWriter) {
        writer.writeByte(id.ordinal.toByte())
        serializeConfig(writer)
    }

    /** Override if your shader has static configuration that needs to be shared with the ShaderImpl. */
    open fun serializeConfig(writer: ByteArrayWriter) {
    }

    private fun toBytes(): ByteArray {
        val writer = ByteArrayWriter()
        serialize(writer)
        return writer.toBytes()
    }

    abstract fun readBuffer(reader: ByteArrayReader): B

    companion object {
        fun parse(reader: ByteArrayReader): Shader<*> {
            val shaderTypeI = reader.readByte()
            val shaderType = ShaderId.get(shaderTypeI)
            return shaderType.parser(reader)
        }
    }
}

interface ShaderBuffer {
    val shader: Shader<*>

    fun serialize(writer: ByteArrayWriter)

    /**
     * Read new data into an existing buffer (as efficiently as possible).
     */
    fun read(reader: ByteArrayReader)
}

interface ShaderImpl<B : ShaderBuffer> {
    fun draw(buffer: B)
}

interface Pixels {
    val count: Int

    fun set(colors: Array<Color>)
}
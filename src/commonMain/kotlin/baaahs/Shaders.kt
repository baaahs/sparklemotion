package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.shaders.*

enum class ShaderType(val parser: (reader: ByteArrayReader) -> Shader) {
    SOLID({ reader -> SolidShader.parse(reader) }),
    PIXEL({ reader -> PixelShader.parse(reader) }),
    SINE_WAVE({ reader -> SineWaveShader.parse(reader) }),
    COMPOSITOR({ reader -> CompositorShader.parse(reader) }),
    SPARKLE({ reader -> SparkleShader.parse(reader) });

    companion object {
        val values = values()
        fun get(i: Byte): ShaderType {
            if (i > values.size || i < 0) {
                throw Throwable("bad index for ShaderType: ${i}")
            }
            return values[i.toInt()]
        }
    }
}

abstract class Shader(val type: ShaderType) {
    abstract val buffer: ShaderBuffer

    open fun serialize(writer: ByteArrayWriter) {
        writer.writeByte(type.ordinal.toByte())
    }

    open fun serializeBuffer(writer: ByteArrayWriter) {
        buffer.serialize(writer)
    }

    abstract fun createImpl(pixels: Pixels): ShaderImpl

    open fun readBuffer(reader: ByteArrayReader) {
        buffer.read(reader)
    }

    companion object {
        fun parse(reader: ByteArrayReader): Shader {
            val shaderTypeI = reader.readByte()
            val shaderType = ShaderType.get(shaderTypeI)
            return shaderType.parser(reader)
        }
    }
}

interface ShaderBuffer {
    fun serialize(writer: ByteArrayWriter)

    fun read(reader: ByteArrayReader)
}

interface ShaderImpl {
    fun draw()
}

interface Pixels {
    val count: Int

    fun set(colors: Array<Color>)
}
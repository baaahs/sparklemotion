package baaahs

enum class ShaderType {
    SOLID,
    PIXEL
}

abstract class ShaderBuffer(val type: ShaderType) {
    abstract fun serialize(writer: ByteArrayWriter)
}

class SolidShaderBuffer : ShaderBuffer(ShaderType.SOLID) {
    var color: Color = Color.WHITE

    override fun serialize(writer: ByteArrayWriter) {
        color.serialize(writer)
    }

    companion object {
        fun parse(reader: ByteArrayReader): SolidShaderBuffer {
            val buf = SolidShaderBuffer()
            buf.color = Color.parse(reader)
            return buf
        }
    }
}

class PixelShaderBuffer : ShaderBuffer(ShaderType.PIXEL) {
    var fakeyTerribleHardCodedNumberOfPixels: Int = 1337
    var colors: MutableList<Color> = ((0..fakeyTerribleHardCodedNumberOfPixels).map { Color.WHITE }).toMutableList()

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeInt(colors.size)
        colors.forEach { color -> color.serialize(writer) }
    }

    companion object {
        fun parse(reader: ByteArrayReader): PixelShaderBuffer {
            val buf = PixelShaderBuffer()
            (0 until reader.readInt()).forEach { index -> buf.colors[index] = Color.parse(reader) }
            return buf
        }
    }
}

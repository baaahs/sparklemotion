package baaahs.shaders

import baaahs.*
import baaahs.glshaders.GlslProgram
import baaahs.glsl.GlslBase
import baaahs.glsl.GlslSurface
import baaahs.glsl.UvTranslator
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlinx.serialization.json.JsonObject

class GlslShader(
    private val glslProgram: GlslProgram,
    private val uvTranslator: UvTranslator
) : Shader<GlslShader.Buffer>(ShaderId.GLSL_SHADER) {

    companion object : ShaderReader<GlslShader> {
        val renderContext by lazy { GlslBase.manager.createContext() }

        override fun parse(reader: ByteArrayReader): GlslShader = TODO("nope")
    }

    override fun serializeConfig(writer: ByteArrayWriter) {
        TODO("nope")
    }

    override fun createRenderer(surface: Surface, renderContext: RenderContext): Renderer {
        val poolKey = GlslShader::class to glslProgram
        val pooledRenderer = renderContext.registerPooled(poolKey) { PooledRenderer(glslProgram, uvTranslator) }
        val glslSurface = pooledRenderer.glslRenderer.addSurface(surface)
        return Renderer(glslSurface)
    }

    override fun createRenderer(surface: Surface): Renderer {
        val glslRenderer = renderContext.createRenderer(glslProgram, uvTranslator)
        val glslSurface = glslRenderer.addSurface(surface)
        return Renderer(glslSurface)
    }

    class Renderer(private val glslSurface: GlslSurface?) : Shader.Renderer<Buffer> {
        override fun beginFrame(buffer: Buffer, pixelCount: Int) {
            // update uniforms from buffer...
//            glslSurface?.uniforms?.updateFrom(buffer.values)
        }

        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            return if (glslSurface != null) glslSurface.pixels[pixelIndex] else Color.BLACK
        }
    }

    class PooledRenderer(program: GlslProgram, uvTranslator: UvTranslator) : baaahs.PooledRenderer {
        val glslRenderer = renderContext.createRenderer(program, uvTranslator)

        override fun preDraw() {
            glslRenderer.draw()
        }
    }

    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*> get() = this@GlslShader

//        val values = Array<Any?>(patch.uniformInputs.size) { }

        fun update(values: List<Any?>) {
//            values.forEachIndexed { index, value -> this.values[index] = value }
        }

        override fun serialize(writer: ByteArrayWriter) {
            TODO("nope")
        }

        override fun read(reader: ByteArrayReader) {
            TODO("nope")
        }
    }

    class Param(val varName: String, val gadgetType: String, val valueType: Type, val config: JsonObject) {
        enum class Type { INT, FLOAT, VEC3 }

        fun serializeConfig(writer: ByteArrayWriter) {
            writer.writeString(varName)
            writer.writeByte(valueType.ordinal.toByte())
        }

        fun serializeValue(value: Any?, writer: ByteArrayWriter) {
            when (valueType) {
                Type.INT -> writer.writeInt(value as Int? ?: 0)
                Type.FLOAT -> writer.writeFloat(value as Float)
                Type.VEC3 -> writer.writeInt((value as Color? ?: Color.WHITE).argb)
            }
        }

        fun readValue(reader: ByteArrayReader): Any {
            return when (valueType) {
                Type.INT -> reader.readInt()
                Type.FLOAT -> reader.readFloat()
                Type.VEC3 -> Color(reader.readInt())
            }
        }

        companion object {
            private val types = Type.values()

            fun parse(reader: ByteArrayReader): Param {
                val varName = reader.readString()
                val valueType = types[reader.readByte().toInt()]
                return Param(varName, "", valueType, JsonObject(emptyMap()))
            }
        }
    }
}

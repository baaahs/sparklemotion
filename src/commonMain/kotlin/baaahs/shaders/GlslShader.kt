package baaahs.shaders

import baaahs.*
import baaahs.Shader
import baaahs.glsl.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlinx.serialization.json.JsonObject

class GlslShader(
    private val program: Program,
    private val uvTranslator: UvTranslator
) : Shader<GlslShader.Buffer>(ShaderId.GLSL_SHADER) {
    companion object : ShaderReader<GlslShader> {
        val logger = Logger("GlslShader")

        override fun parse(reader: ByteArrayReader): GlslShader {
            val glslProgram = reader.readString()
            val program = GlslBase.manager.createProgram(glslProgram)
            val uvTranslator = UvTranslator.parse(reader)
            return GlslShader(program, uvTranslator)
        }
    }

    override fun serializeConfig(writer: ByteArrayWriter) {
        writer.writeString(program.fragShader)
    }

    override fun createRenderer(surface: Surface, renderContext: RenderContext): Renderer {
        val poolKey = GlslShader::class to program
        val pooledRenderer = renderContext.registerPooled(poolKey) { PooledRenderer(program, uvTranslator) }
        val glslSurface = pooledRenderer.glslRenderer.addSurface(surface)
        return Renderer(glslSurface)
    }

    override fun createRenderer(surface: Surface): Renderer {
        val glslRenderer = GlslBase.manager.createRenderer(program, uvTranslator)
        val glslSurface = glslRenderer.addSurface(surface)
        return Renderer(glslSurface)
    }

    class Renderer(private val glslSurface: GlslSurface?) : Shader.Renderer<Buffer> {
        override fun beginFrame(buffer: Buffer, pixelCount: Int) {
            // update uniforms from buffer...
            glslSurface?.uniforms?.updateFrom(buffer.values)
        }

        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            return if (glslSurface != null) glslSurface.pixels[pixelIndex] else Color.BLACK
        }
    }

    class PooledRenderer(program: Program, uvTranslator: UvTranslator) : baaahs.PooledRenderer {
        val glslRenderer = GlslBase.manager.createRenderer(program, uvTranslator)

        override fun preDraw() {
            glslRenderer.draw()
        }
    }

    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*> get() = this@GlslShader

        val values = Array<Any?>(program.params.size) { }

        fun update(values: List<Any?>) {
            values.forEachIndexed { index, value -> this.values[index] = value }
        }

        override fun serialize(writer: ByteArrayWriter) {
            uvTranslator.serialize(writer)

            program.params.zip(values).forEach { (param, value) -> param.serializeValue(value, writer) }
        }

        override fun read(reader: ByteArrayReader) {
            program.params.forEachIndexed { index, param -> values[index] = param.readValue(reader) }
        }
    }

    data class Param(
        val varName: String,
        val dataSourceProvider: GlslPlugin.DataSourceProvider,
        val valueType: Type,
        val config: JsonObject
    ) {
        enum class Type { INT, FLOAT, VEC3 }

        fun serializeConfig(writer: ByteArrayWriter) {
            writer.writeString(varName)
            writer.writeString(dataSourceProvider.category.name)
            writer.writeString(dataSourceProvider.name)
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
                val categoryName = reader.readString()
                val sourceName = reader.readString()
                val dataSourceProvider = GlslPlugin.DataSourceProvider.from(categoryName, sourceName)
                val valueType = types[reader.readByte().toInt()]
                return Param(varName, dataSourceProvider, valueType, JsonObject(emptyMap()))
            }
        }
    }
}

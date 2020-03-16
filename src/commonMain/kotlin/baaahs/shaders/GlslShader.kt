package baaahs.shaders

import baaahs.Color
import baaahs.Surface
import baaahs.glsl.GlslBase
import baaahs.glsl.GlslSurface
import baaahs.glsl.UvTranslator
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json

class GlslShader(
    private val glslProgram: String,
    private val uvTranslator: UvTranslator,
    val adjustableValues: List<AdjustableValue> = findAdjustableValues(glslProgram)
) : Shader<GlslShader.Buffer>(ShaderId.GLSL_SHADER) {

    companion object : ShaderReader<GlslShader> {
        override fun parse(reader: ByteArrayReader): GlslShader {
            val glslProgram = reader.readString()
            val uvTranslator = UvTranslator.parse(reader)
            val adjustableValueCount = reader.readShort()
            val adjustableValues = (0 until adjustableValueCount).map { AdjustableValue.parse(reader) }
            return GlslShader(glslProgram, uvTranslator, adjustableValues)
        }

        private val json = Json(JsonConfiguration.Stable.copy(isLenient = true))
        private val gadgetPattern = Regex(
            "\\s*//\\s*SPARKLEMOTION GADGET:\\s*([^\\s]+)\\s+(\\{.*})\\s*\n" +
                    "\\s*uniform\\s+([^\\s]+)\\s+([^\\s]+);"
        )

        val extraAdjustables = listOf(
            AdjustableValue(
                "sm_uScale", "Slider", AdjustableValue.Type.FLOAT,
                json { "name" to "u scale"; "minValue" to 0f; "maxValue" to 3f }),
            AdjustableValue(
                "sm_vScale", "Slider", AdjustableValue.Type.FLOAT,
                json { "name" to "v scale"; "minValue" to 0f; "maxValue" to 3f }),
            AdjustableValue(
                "sm_beat", "Beat", AdjustableValue.Type.FLOAT,
                json { "name" to "beat" }),
            AdjustableValue(
                "sm_startOfMeasure", "StartOfMeasure", AdjustableValue.Type.FLOAT,
                json { "name" to "startOfMeasure"; }),
            AdjustableValue(
                "sm_brightness", "Slider", AdjustableValue.Type.FLOAT,
                json { "name" to "brightness"; "minValue" to 0f; "maxValue" to 1f }),
            AdjustableValue(
                "sm_saturation", "Slider", AdjustableValue.Type.FLOAT,
                json { "name" to "saturation"; "minValue" to 0f; "maxValue" to 1f })
        )

        fun findAdjustableValues(glslFragmentShader: String): List<AdjustableValue> {
            return gadgetPattern.findAll(glslFragmentShader).map { matchResult ->
                println("matches: ${matchResult.groupValues}")
                val (gadgetType, configJson, valueTypeName, varName) = matchResult.destructured
                val configData = json.parseJson(configJson)
                val valueType = when (valueTypeName) {
                    "int" -> AdjustableValue.Type.INT
                    "float" -> AdjustableValue.Type.FLOAT
                    "vec3" -> AdjustableValue.Type.VEC3
                    else -> throw IllegalArgumentException("unsupported type $valueTypeName")
                }
                AdjustableValue(varName, gadgetType, valueType, configData.jsonObject)
            }.toList() + extraAdjustables
        }
    }

    override fun serializeConfig(writer: ByteArrayWriter) {
        writer.writeString(glslProgram)
        writer.writeShort(adjustableValues.size)
        adjustableValues.forEach { it.serializeConfig(writer) }
    }

    override fun createRenderer(surface: Surface, renderContext: RenderContext): Renderer {
        val poolKey = GlslShader::class to glslProgram
        val pooledRenderer =
            renderContext.registerPooled(poolKey) { PooledRenderer(glslProgram, uvTranslator, adjustableValues) }
        val glslSurface = pooledRenderer.glslRenderer.addSurface(surface)
        return Renderer(glslSurface)
    }

    override fun createRenderer(surface: Surface): Renderer {
        val glslRenderer = GlslBase.manager.createRenderer(glslProgram, uvTranslator, adjustableValues)
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

    class PooledRenderer(
        glslProgram: String, uvTranslator: UvTranslator, adjustableValues: List<AdjustableValue>
    ) : baaahs.shaders.PooledRenderer {
        val glslRenderer = GlslBase.manager.createRenderer(glslProgram, uvTranslator, adjustableValues)

        override fun preDraw() {
            glslRenderer.draw()
        }
    }

    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*> get() = this@GlslShader

        val values = Array<Any?>(adjustableValues.size) { }

        fun update(values: List<Any?>) {
            values.forEachIndexed { index, value -> this.values[index] = value }
        }

        override fun serialize(writer: ByteArrayWriter) {
            uvTranslator.serialize(writer)

            adjustableValues.zip(values).forEach { (adjustableValue, value) ->
                adjustableValue.serializeValue(value, writer)
            }
        }

        override fun read(reader: ByteArrayReader) {
            adjustableValues.forEachIndexed { index, adjustableValue ->
                values[index] = adjustableValue.readValue(reader)
            }
        }
    }

    class AdjustableValue(val varName: String, val gadgetType: String, val valueType: Type, val config: JsonObject) {
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

            fun parse(reader: ByteArrayReader): AdjustableValue {
                val varName = reader.readString()
                val valueType = types[reader.readByte().toInt()]
                return AdjustableValue(varName, "", valueType, JsonObject(emptyMap()))
            }
        }
    }
}

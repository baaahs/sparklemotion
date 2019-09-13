package baaahs.shaders

import baaahs.*
import baaahs.glsl.GlslBase
import baaahs.glsl.GlslSurface
import baaahs.glsl.ModelSpaceUvTranslator
import baaahs.glsl.PanelSpaceUvTranslator
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import de.fabmax.kool.shading.Uniform
import de.fabmax.kool.shading.Uniform1f
import de.fabmax.kool.shading.Uniform1i
import de.fabmax.kool.shading.Uniform3f
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json

class GlslShader(
    val glslProgram: String,
    val adjustableValues: List<AdjustableValue> = findAdjustableValues(glslProgram)
) : Shader<GlslShader.Buffer>(ShaderId.GLSL_SHADER) {

    companion object : ShaderReader<GlslShader> {
        var model_CHEAT: Model<*>? = null

        override fun parse(reader: ByteArrayReader): GlslShader {
            val glslProgram = reader.readString()
            val adjustableValueCount = reader.readShort()
            val adjustableValues = (0 until adjustableValueCount).map { i -> AdjustableValue.parse(reader, i) }
            return GlslShader(glslProgram, adjustableValues)
        }

        private val json = Json(JsonConfiguration.Stable.copy(strictMode = false))
        private val gadgetPattern = Regex(
            "\\s*//\\s*SPARKLEMOTION GADGET:\\s*([^\\s]+)\\s+(\\{.*})\\s*\n" +
                "\\s*uniform\\s+([^\\s]+)\\s+([^\\s]+);"
        )

        val extraAdjustables = listOf(
            AdjustableValue("sm_uScale", "Slider", AdjustableValue.Type.FLOAT,
                json { "name" to "u scale"; "minValue" to 0f; "maxValue" to 3f }, 0),
            AdjustableValue("sm_vScale", "Slider", AdjustableValue.Type.FLOAT,
                json { "name" to "v scale"; "minValue" to 0f; "maxValue" to 3f }, 1),
            AdjustableValue("sm_beat", "Beat", AdjustableValue.Type.FLOAT,
                json { "name" to "beat" }, 2),
            AdjustableValue("sm_startOfMeasure", "StartOfMeasure", AdjustableValue.Type.FLOAT,
                json { "name" to "startOfMeasure"; }, 3
            ),
            AdjustableValue("sm_brightness", "Slider", AdjustableValue.Type.FLOAT,
                json { "name" to "brightness"; "minValue" to 0f; "maxValue" to 1f }, 4
            ),
            AdjustableValue("sm_saturation", "Slider", AdjustableValue.Type.FLOAT,
                json { "name" to "saturation"; "minValue" to 0f; "maxValue" to 1f }, 4
            )
        )

        fun findAdjustableValues(glslFragmentShader: String): List<AdjustableValue> {
            var i = (extraAdjustables.map { it.ordinal }.max() ?: -1) + 1

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
                AdjustableValue(varName, gadgetType, valueType, configData.jsonObject, i++)
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
        val pooledRenderer = renderContext.registerPooled(poolKey) { PooledRenderer(glslProgram, adjustableValues) }
        val uvTranslator = ModelSpaceUvTranslator(model_CHEAT!!)
        val glslSurface = pooledRenderer.glslRenderer.addSurface(surface, uvTranslator)
        return Renderer(glslSurface)
    }

    override fun createRenderer(surface: Surface): Renderer {
        val glslRenderer = GlslBase.manager.createRenderer(glslProgram, adjustableValues)
        val glslSurface = glslRenderer.addSurface(surface, PanelSpaceUvTranslator)
        return Renderer(glslSurface)
    }

    class Renderer(private val glslSurface: GlslSurface?) : Shader.Renderer<Buffer> {
        override fun beginFrame(buffer: Buffer, pixelCount: Int) {
            // update uniforms from buffer...
            if (glslSurface != null) {
                glslSurface.uniforms.updateFrom(buffer.values)
            }
        }

        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            return if (glslSurface != null) glslSurface.pixels[pixelIndex] else Color.BLACK
        }
    }

    class PooledRenderer(glslProgram: String, adjustableValues: List<AdjustableValue>) : baaahs.PooledRenderer {
        val glslRenderer = GlslBase.manager.createRenderer(glslProgram, adjustableValues)

        override fun preDraw() {
            glslRenderer.draw()
        }
    }

    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*> get() = this@GlslShader

        val values = Array<Any?>(adjustableValues.size) { }

        fun update(adjustableValue: AdjustableValue, value: Any) {
            values[adjustableValue.ordinal] = value
        }

        override fun serialize(writer: ByteArrayWriter) {
            adjustableValues.forEach { it.serializeValue(values[it.ordinal], writer) }
        }

        override fun read(reader: ByteArrayReader) {
            adjustableValues.forEach { values[it.ordinal] = it.readValue(reader) }
        }
    }

    class AdjustableValue(
        val varName: String,
        val gadgetType: String,
        val valueType: Type,
        val config: JsonObject,
        val ordinal: Int
    ) {
        enum class Type {
            INT {
                override fun getUniform(varName: String): Uniform<*> = Uniform1i(varName)
            },
            FLOAT {
                override fun getUniform(varName: String): Uniform<*> = Uniform1f(varName)
            },
            VEC3 {
                override fun getUniform(varName: String): Uniform<*> = Uniform3f(varName)
            };

            abstract fun getUniform(varName: String): Uniform<*>
        }

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

        fun getUniform(): Uniform<*> {
            return valueType.getUniform(varName)
        }

        companion object {
            private val types = Type.values()

            fun parse(reader: ByteArrayReader, ordinal: Int): AdjustableValue {
                val varName = reader.readString()
                val valueType = types[reader.readByte().toInt()]
                return AdjustableValue(varName, "", valueType, JsonObject(emptyMap()), ordinal)
            }
        }
    }
}

package baaahs.glshaders

import baaahs.glshaders.GlslCode.GlslFunction
import baaahs.glshaders.GlslCode.Namespace
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.emptyMap
import kotlin.collections.forEach
import kotlin.collections.hashMapOf
import kotlin.collections.plus
import kotlin.collections.set
import kotlin.collections.toSet

interface ShaderFragment {
    enum class Type(val outContentType: GlslCode.ContentType) {
        Color(GlslCode.ContentType.Color),
        Projection(GlslCode.ContentType.UvCoordinate),
        Transformer(GlslCode.ContentType.UvCoordinate),
        Filter(GlslCode.ContentType.Color)
    }

    val src: String get() = glslCode.src
    val glslCode: GlslCode
    val title: String
    val description: String?
    val shaderType: Type
    val entryPoint: GlslFunction
    val inputPorts: List<InputPort>
    val outputPorts: List<OutputPort>
//    TODO val inputDefaults: Map<String, InputDefault>

    fun toGlsl(namespace: Namespace, portMap: Map<String, String> = emptyMap()): String
    fun invocationGlsl(namespace: Namespace, portMap: Map<String, String> = emptyMap()): String

    abstract class Base(final override val glslCode: GlslCode) : ShaderFragment {
        override val title: String = glslCode.title
        override val description: String? = null

        override fun toGlsl(namespace: Namespace, portMap: Map<String, String>): String {
            val buf = StringBuilder()

            val nonUniformGlobalsMap = hashMapOf<String, String>()
            glslCode.globalVars.forEach { glslVar ->
                if (!glslVar.isUniform) {
                    nonUniformGlobalsMap[glslVar.name] = namespace.qualify(glslVar.name)
                    buf.append(glslVar.toGlsl(namespace, glslCode.symbolNames, emptyMap()))
                    buf.append("\n")
                }
            }

            val symbolsToNamespace = glslCode.symbolNames.toSet()
            val symbolMap = portMap + nonUniformGlobalsMap
            glslCode.functions.forEach { glslFunction ->
                buf.append(glslFunction.toGlsl(namespace, symbolsToNamespace, symbolMap))
                buf.append("\n")
            }

            return buf.toString()
        }
    }

    companion object {
        fun tryColorShader(glslCode: GlslCode): ColorShader? {
            return when {
                glslCode.functionNames.contains("main") ->
                    GenericColorShader(glslCode)

                glslCode.functionNames.contains("mainImage") ->
                    ShaderToyColorShader(glslCode)

                else -> null
            }
        }

        fun tryUvTranslatorShader(glslCode: GlslCode): UvShader? {
            return when {
                glslCode.functionNames.contains("mainUvFromRaster") ->
                    UvShader(glslCode)

                else -> null
            }
        }
    }
}

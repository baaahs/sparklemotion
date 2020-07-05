package baaahs.glshaders

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.glshaders.GlslCode.GlslFunction
import baaahs.glshaders.GlslCode.Namespace
import baaahs.show.Shader
import kotlin.collections.set

interface OpenShader : RefCounted {
    enum class Type(val sortOrder: Int) {
        Color(1),
        Projection(0),
        Transformer(-1),
        Filter(-1);
    }

    val shader: Shader get() = Shader(src)
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

    abstract class Base(final override val glslCode: GlslCode) : OpenShader, RefCounted by RefCounter() {
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

        override fun equals(other: Any?): Boolean =
            other != null
                    && other is Base
                    && this::class == other::class
                    && this.src == other.src

        override fun hashCode(): Int =
            src.hashCode()
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
package baaahs.glshaders

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.glshaders.GlslCode.GlslFunction
import baaahs.glshaders.GlslCode.Namespace
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.unknown
import kotlin.collections.set

interface OpenShader : RefCounted {
    val shader: Shader
    val src: String get() = glslCode.src
    val glslCode: GlslCode
    val title: String
    val description: String?
    val shaderType: ShaderType
    val entryPoint: GlslFunction
    val inputPorts: List<InputPort>
    val outputPort: OutputPort
//    TODO val inputDefaults: Map<String, InputDefault>

    fun findInputPort(portId: String): InputPort
    fun toGlsl(namespace: Namespace, portMap: Map<String, String> = emptyMap()): String
    fun invocationGlsl(
        namespace: Namespace,
        resultVar: String,
        portMap: Map<String, String> = emptyMap()
    ): String

    abstract class Base(
        final override val shader: Shader,
        final override val glslCode: GlslCode
    ) : OpenShader, RefCounted by RefCounter() {
        override val title: String = shader.title
        override val description: String? = null

        protected fun toInputPort(it: GlslCode.GlslVar): InputPort {
            return InputPort(
                it.name, it.dataType, it.name.capitalize(),
                pluginRef = it.hint?.pluginRef,
                pluginConfig = it.hint?.config,
                glslVar = it
            )
        }

        override fun findInputPort(portId: String): InputPort {
            return inputPorts.find { it.id == portId }
                ?: error(unknown("input port", portId, inputPorts))
        }

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
        fun tryColorShader(shader: Shader, glslCode: GlslCode): ColorShader? {
            return when {
                glslCode.functionNames.contains("main") ->
                    GenericColorShader(shader, glslCode)

                glslCode.functionNames.contains("mainImage") ->
                    ShaderToyColorShader(shader, glslCode)

                else -> null
            }
        }

        fun tryFilterShader(shader: Shader, glslCode: GlslCode): FilterShader? {
            return when {
                glslCode.functionNames.contains("filterImage") ->
                    FilterShader(shader, glslCode)

                else -> null
            }
        }

        fun tryUvTranslatorShader(shader: Shader, glslCode: GlslCode): UvShader? {
            return when {
                glslCode.functionNames.contains("mainUvFromRaster") ->
                    UvShader(shader, glslCode)

                else -> null
            }
        }
    }
}
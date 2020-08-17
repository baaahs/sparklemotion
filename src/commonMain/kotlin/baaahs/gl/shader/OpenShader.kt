package baaahs.gl.shader

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslCode.GlslFunction
import baaahs.gl.glsl.GlslCode.Namespace
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.unknown
import kotlin.collections.set

interface OpenShader : RefCounted {
    val shader: Shader
    val src: String get() = glslCode.src
    val glslCode: GlslCode
    val title: String
    val shaderType: ShaderType
    val entryPointName: String
    val entryPoint: GlslFunction
        get() = glslCode.findFunction(entryPointName)

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
        final override val glslCode: GlslCode,
        private val plugins: Plugins
    ) : OpenShader, RefCounted by RefCounter() {
        override val title: String get() = shader.title

        abstract val proFormaInputPorts: List<InputPort>
        abstract val wellKnownInputPorts: Map<String, InputPort>

        override val inputPorts: List<InputPort> by lazy {
            proFormaInputPorts +
                    glslCode.globalInputVars.map {
                        wellKnownInputPorts[it.name]
                            ?.copy(type = it.type, glslVar = it)
                            ?: toInputPort(it)
                    }
        }

        protected fun toInputPort(it: GlslCode.GlslVar): InputPort {
            return InputPort(
                it.name, it.type, it.displayName(),
                pluginRef = it.hint?.pluginRef,
                pluginConfig = it.hint?.config,
                contentType = it.hint?.tag("type")?.let { plugins.resolveContentType(it) },
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
                if (!glslVar.isUniform && !glslVar.isVarying) {
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
}
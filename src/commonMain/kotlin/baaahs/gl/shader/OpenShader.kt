package baaahs.gl.shader

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslCode.GlslFunction
import baaahs.gl.glsl.GlslCode.Namespace
import baaahs.gl.glsl.GlslType
import baaahs.gl.glsl.LinkException
import baaahs.gl.patch.ContentType
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
    ): String {
        return "$resultVar = ${namespace.qualify(entryPoint.name)}" +
                "(${entryPoint.params.map {
                    portMap[it.name] ?: throw LinkException("No ${it.name} input for shader \"$title\"")
                }})"
    }

    abstract class Base(
        final override val shader: Shader,
        final override val glslCode: GlslCode,
        private val plugins: Plugins
    ) : OpenShader, RefCounted by RefCounter() {
        override val title: String get() = shader.title

        abstract val argInputPorts: Map<GlslType, ContentType>
        abstract val implicitInputPorts: List<InputPort>
        abstract val wellKnownInputPorts: Map<String, InputPort>

        override val inputPorts: List<InputPort> by lazy {
            implicitInputPorts +
                    inputPortsFromEntryPointParams() +
                    glslCode.globalInputVars.map { glslVar ->
                        wellKnownInputPorts[glslVar.name]
                            ?.copy(type = glslVar.type, glslField = glslVar)
                            ?: glslVar.toInputPort()
                    }
        }

        protected fun inputPortsFromEntryPointParams(): List<InputPort> {
            val argInputPorts = argInputPorts.toMutableMap()
            return entryPoint.params
                .filter { it.isIn }
                .map { glslParam -> glslParam.toInputPort(argInputPorts) }
        }

        protected fun GlslCode.GlslVar.toInputPort(): InputPort {
            return InputPort(
                name, type, displayName(),
                pluginRef = hint?.pluginRef,
                pluginConfig = hint?.config,
                contentType = hint?.tag("type")?.let { plugins.resolveContentType(it) },
                glslField = this
            )
        }

        protected fun GlslCode.GlslParam.toInputPort(contentTypeHints: MutableMap<GlslType, ContentType>): InputPort {
            return InputPort(
                name, type, displayName(),
                pluginRef = hint?.pluginRef,
                pluginConfig = hint?.config,
                contentType = hint?.tag("type")?.let { plugins.resolveContentType(it) }
                    ?: contentTypeHints.remove(type),
                glslField = this,
                isParametric = true
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
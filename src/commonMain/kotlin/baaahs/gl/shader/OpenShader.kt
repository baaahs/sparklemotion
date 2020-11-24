package baaahs.gl.shader

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslCode.GlslFunction
import baaahs.gl.glsl.GlslCode.Namespace
import baaahs.gl.patch.ContentType
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.unknown
import kotlin.collections.set

interface OpenShader : RefCounted {
    val shader: Shader
    val src: String get() = glslCode.src
    val glslCode: GlslCode
    val title: String
    val entryPointName: String
    val entryPoint: GlslFunction
        get() = glslCode.findFunction(entryPointName)

    val inputPorts: List<InputPort>
    val outputPort: OutputPort
    val defaultPriority: Int
    val defaultUpstreams: Map<ContentType, ShaderChannel>

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
                            ?.copy(type = it.type, glslArgSite = it)
                            ?: it.toInputPort(plugins)
                    }
        }

        abstract val shaderType: ShaderType
        override val defaultPriority: Int
            get() = shaderType.priority
        override val defaultUpstreams: Map<ContentType, ShaderChannel>
            get() = shaderType.defaultUpstreams

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
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
    val entryPoint: GlslFunction

    val inputPorts: List<InputPort>
    val outputPort: OutputPort
    val defaultPriority: Int
    val defaultUpstreams: Map<ContentType, ShaderChannel>

    fun findInputPortOrNull(portId: String): InputPort?
    fun findInputPort(portId: String): InputPort =
        findInputPortOrNull(portId)
            ?: error(unknown("input port", portId, inputPorts))

    fun toGlsl(namespace: Namespace, portMap: Map<String, String> = emptyMap()): String

    fun invocationGlsl(
        namespace: Namespace,
        resultVar: String,
        portMap: Map<String, String> = emptyMap()
    ): String

    class Base(
        override val shader: Shader,
        override val glslCode: GlslCode,
        prototype: ShaderPrototype?,
        private val plugins: Plugins
    ) : OpenShader, RefCounted by RefCounter() {
        private val prototype = prototype ?: GenericShaderPrototype
        override val title: String get() = shader.title
        override val entryPoint: GlslFunction = this.prototype.findEntryPoint(glslCode)

        private val proFormaInputPorts: List<InputPort>
            get() = prototype.implicitInputPorts.mapNotNull { ifRefersTo(it)?.copy(isImplicit = true) }

        private val wellKnownInputPorts = this.prototype.wellKnownInputPorts.associateBy { it.id }
        private val defaultInputPortsByType = this.prototype.defaultInputPortsByType

        override val inputPorts: List<InputPort> by lazy {
            proFormaInputPorts +
                    (glslCode.globalInputVars + entryPoint.params.filter { it.isIn })
                        .map {
                            wellKnownInputPorts[it.name]?.copy(type = it.type, glslArgSite = it)
                                ?: defaultInputPortsByType[it.type to it.isVarying]
                                    ?.copy(id = it.name, varName = it.name, glslArgSite = it)
                                ?: it.toInputPort(plugins)
                        } +
                    this.prototype.findMagicInputPorts(glslCode)
        }
        override val outputPort: OutputPort
            get() = prototype.findOutputPort(glslCode, plugins)
        val shaderType: ShaderType = this.prototype.shaderType
        override val defaultPriority: Int
            get() = shaderType.priority
        override val defaultUpstreams: Map<ContentType, ShaderChannel>
            get() = prototype.defaultUpstreams

        override fun findInputPortOrNull(portId: String): InputPort? =
            inputPorts.find { it.id == portId }

        fun ifRefersTo(inputPort: InputPort) =
            if (glslCode.refersToGlobal(inputPort.id)) inputPort else null

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

            val uniformGlobalsMap = portMap.filter { (id, _) ->
                val inputPort = findInputPortOrNull(id)

                inputPort?.isGlobal == true || outputPort.id == id
            }

            val symbolsToNamespace = glslCode.symbolNames.toSet()
            val symbolMap = uniformGlobalsMap + nonUniformGlobalsMap
            glslCode.functions.forEach { glslFunction ->
                buf.append(glslFunction.toGlsl(namespace, symbolsToNamespace, symbolMap))
                buf.append("\n")
            }

            return buf.toString()
        }

        override fun invocationGlsl(namespace: Namespace, resultVar: String, portMap: Map<String, String>): String {
            return prototype.invocationGlsl(namespace, resultVar, portMap, entryPoint)
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
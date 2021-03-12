package baaahs.gl.shader

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.gl.glsl.*
import baaahs.gl.glsl.GlslCode.GlslFunction
import baaahs.gl.glsl.GlslCode.Namespace
import baaahs.gl.shader.dialect.ShaderDialect
import baaahs.gl.shader.type.ShaderType
import baaahs.only
import baaahs.show.Shader
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

    val shaderType: ShaderType
    val shaderDialect: ShaderDialect

    val errors: List<GlslError>

    fun findInputPortOrNull(portId: String): InputPort? =
        inputPorts.find { it.id == portId }

    fun findInputPort(portId: String): InputPort =
        findInputPortOrNull(portId)
            ?: error(unknown("input port", portId, inputPorts))

    val portStructs: List<GlslType.Struct> get() =
        (inputPorts.map { it.contentType.glslType } + outputPort.contentType.glslType)
            .filterIsInstance<GlslType.Struct>()

    /** The list of global variables that aren't also backing input ports. */
    val globalVars: List<GlslCode.GlslVar>

    fun toGlsl(namespace: Namespace, portMap: Map<String, GlslExpr> = emptyMap()): String

    fun invoker(
        namespace: Namespace,
        portMap: Map<String, GlslExpr> = emptyMap()
    ): GlslCode.Invoker

    class Base(
        override val shader: Shader,
        override val glslCode: GlslCode,
        override val entryPoint: GlslFunction,
        override val inputPorts: List<InputPort>,
        override val outputPort: OutputPort,
        override val shaderType: ShaderType,
        override val shaderDialect: ShaderDialect,
        override val errors: List<GlslError> = emptyList()
    ) : OpenShader, RefCounted by RefCounter() {

        constructor(shaderAnalysis: ShaderAnalysis, shaderType: ShaderType) : this(
            shaderAnalysis.shader, shaderAnalysis.glslCode, shaderAnalysis.entryPoint!!,
            shaderAnalysis.inputPorts, shaderAnalysis.outputPorts.only(), shaderType,
            shaderAnalysis.shaderDialect
        )

        override val title: String get() = shader.title

        override val globalVars: List<GlslCode.GlslVar> get() =
            glslCode.globalVars.filter { !it.isUniform && !it.isVarying }

        override fun toGlsl(
            namespace: Namespace,
            portMap: Map<String, GlslExpr>
        ): String {
            val buf = StringBuilder()

            val nonUniformGlobalsMap = hashMapOf<String, GlslExpr>()
            globalVars.forEach { glslVar ->
                nonUniformGlobalsMap[glslVar.name] = GlslExpr(namespace.qualify(glslVar.name))
                buf.append(glslVar.toGlsl(namespace, glslCode.symbolNames, emptyMap()))
                buf.append("\n")
            }

            val uniformGlobalsMap = portMap.filter { (id, _) ->
                val inputPort = findInputPortOrNull(id)

                inputPort?.isGlobal == true ||
                        (outputPort.id == id && !outputPort.isParam)
            }

            val symbolsToNamespace = glslCode.symbolNames.toSet() - portStructs.map { it.name}
            val symbolMap = uniformGlobalsMap + nonUniformGlobalsMap
            glslCode.functions.filterNot { it.isAbstract }.forEach { glslFunction ->
                buf.append(glslFunction.toGlsl(namespace, symbolsToNamespace, symbolMap))
                buf.append("\n")
            }

            return buf.toString()
        }

        override fun invoker(namespace: Namespace, portMap: Map<String, GlslExpr>): GlslCode.Invoker {
            return entryPoint.invoker(namespace, portMap)
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
package baaahs.gl.shader

import baaahs.gl.glsl.*
import baaahs.gl.glsl.GlslCode.GlslFunction
import baaahs.gl.glsl.GlslCode.Namespace
import baaahs.gl.shader.dialect.ShaderDialect
import baaahs.gl.shader.type.ShaderType
import baaahs.only
import baaahs.show.Shader
import baaahs.unknown
import baaahs.util.RefCounted
import baaahs.util.RefCounter

interface OpenShader : RefCounted {
    val shader: Shader
    val src: String get() = glslCode.src
    val glslCode: GlslCode
    val title: String
    val requiresInit: Boolean get() = false
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

    fun toGlsl(fileNumber: Int?, substitutions: GlslCode.Substitutions): String

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

        override val requiresInit = globalVars.any { it.deferInitialization }

        override fun toGlsl(fileNumber: Int?, substitutions: GlslCode.Substitutions): String {
            val buf = StringBuilder()
            globalVars.forEach { glslVar ->
                buf.append(glslVar.declarationToGlsl(fileNumber, substitutions))
                buf.append("\n")
            }

            glslCode.functions.filterNot { it.isAbstract }.forEach { glslFunction ->
                buf.append(glslFunction.toGlsl(fileNumber, substitutions))
                buf.append("\n")
            }

            if (requiresInit) {
                buf.append("\n")
                buf.append("void ${substitutions.substitute(ShaderSubstitutions.initFnName)}() {")
                globalVars.forEach {
                    if (it.deferInitialization) {
                        buf.append("    ${it.assignmentToGlsl(fileNumber, substitutions)}\n")
                    }
                }
                buf.append("}\n")
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

class ShaderSubstitutions(
    val openShader: OpenShader,
    val namespace: Namespace,
    portMap: Map<String, GlslExpr>,
    globalStructs: List<GlslType.Struct>,
    passThroughUniforms: List<GlslCode.GlslVar>
) : GlslCode.Substitutions {
    private val uniformGlobalsMap = portMap.filter { (id, _) ->
        val inputPort = openShader.findInputPortOrNull(id)
        inputPort?.isGlobal == true ||
                (openShader.outputPort.id == id && !openShader.outputPort.isParam)
    }

    private val nonUniformGlobalsMap = openShader.globalVars.associate { glslVar ->
        glslVar.name to GlslExpr(namespace.qualify(glslVar.name))
    }

    private val symbolsToNamespace =
        openShader.glslCode.symbolNames.toSet() -
                (
                        openShader.portStructs.map { it.name } +
                                globalStructs.map { it.name } +
                                passThroughUniforms.map { it.name }
                        ).toSet()

    private val specialSymbols =
        mapOf(initFnName to GlslExpr(namespacedInitFnName(namespace)))

    private val symbolMap = uniformGlobalsMap + nonUniformGlobalsMap + specialSymbols

    override fun substitute(word: String): String =
        symbolMap[word]?.s
            ?: if (symbolsToNamespace.contains(word)) {
                namespace.qualify(word)
            } else {
                word
            }

    companion object {
        val initFnName = "_init_"
        fun namespacedInitFnName(namespace: Namespace) = namespace.internalQualify("init")
    }
}

/** The list of global variables that aren't also backing input ports. */
private val OpenShader.globalVars: List<GlslCode.GlslVar> get() =
    glslCode.globalVars.filter { !it.isUniform && !it.isVarying }

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

    /** True iff any input port's content type matches the output content type. */
    val isFilter get() = inputPorts.any { it.contentType == outputPort.contentType }

    fun findInputPortOrNull(portId: String): InputPort? =
        inputPorts.find { it.id == portId }

    fun findInputPort(portId: String): InputPort =
        findInputPortOrNull(portId)
            ?: error(unknown("input port", portId, inputPorts))

    val portStructs: List<GlslType.Struct> get() =
        (inputPorts.map { it.contentType.glslType } + outputPort.contentType.glslType)
            .filterIsInstance<GlslType.Struct>()

    fun toGlsl(fileNumber: Int?, substitutions: ShaderSubstitutions) =
        toGlsl(fileNumber, shaderDialect.buildStatementRewriter(substitutions))

    fun toGlsl(fileNumber: Int?, statementRewriter: GlslCode.StatementRewriter): String

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

        override fun toGlsl(fileNumber: Int?, statementRewriter: GlslCode.StatementRewriter): String =
            buildString {
                shaderDialect.genGlslStatements(glslCode).forEach { glslStatement ->
                    append(glslStatement.toGlsl(fileNumber, statementRewriter))
                    append("\n")
                }

                if (requiresInit) {
                    append("\n")
                    append("void ${statementRewriter.substitute(ShaderSubstitutions.initFnName)}() {")
                    globalVars.forEach {
                        if (it.deferInitialization) {
                            append("    ${it.assignmentToGlsl(fileNumber, statementRewriter)}\n")
                        }
                    }
                    append("}\n")
                }
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

open class ShaderStatementRewriter(
    private val shaderSubstitutions: ShaderSubstitutions
) : GlslCode.StatementRewriter {
    private val buf = StringBuilder()
    protected var inComment = false
    protected var inDotTraversal = false

    override fun visit(token: String): GlslParser.Tokenizer.State {
        when (token) {
            "//" -> {
                inComment = true; buf.append(token)
            }
            "." -> {
                if (!inComment) inDotTraversal = true; buf.append(token)
            }
            "\n" -> {
                inComment = false; buf.append(token)
            }
            else -> {
                buf.append(
                    if (inComment || inDotTraversal) token
                    else shaderSubstitutions.substitute(token)
                )
                inDotTraversal = false
            }
        }
        return this
    }

    override fun substitute(text: String): String =
        shaderSubstitutions.substitute(text)

    override fun drain(): String =
        buf.toString().also { buf.clear() }
}

class ShaderSubstitutions(
    val openShader: OpenShader,
    val namespace: Namespace,
    portMap: Map<String, GlslExpr>
) {
    private val uniformGlobalsMap = portMap.filter { (id, _) ->
        val inputPort = openShader.findInputPortOrNull(id)
        inputPort?.isGlobal == true ||
                (openShader.outputPort.id == id && !openShader.outputPort.isParam)
    }

    private val nonUniformGlobalsMap = openShader.globalVars.associate { glslVar ->
        glslVar.name to GlslExpr(namespace.qualify(glslVar.name))
    }

    private val symbolsToNamespace =
        openShader.glslCode.symbolNames.toSet() - openShader.portStructs.map { it.name }

    private val specialSymbols =
        mapOf(initFnName to GlslExpr(namespacedInitFnName(namespace)))

    private val symbolMap = uniformGlobalsMap + nonUniformGlobalsMap + specialSymbols

    fun substitute(word: String): String =
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

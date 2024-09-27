package baaahs.gl.shader.dialect

import baaahs.gl.glsl.*
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.plugin.Plugins
import baaahs.show.Shader

abstract class BaseShaderAnalyzer(
    protected val glslCode: GlslCode,
    protected val plugins: Plugins,
) : ShaderAnalyzer {
    open val implicitInputPorts: List<InputPort> = emptyList()
    open val wellKnownInputPorts: List<InputPort>
        get() = dialect.wellKnownInputPorts
    open val defaultInputPortsByType: Map<GlslType, InputPort> = emptyMap()
    abstract val entryPointName: String

    override val matchLevel: MatchLevel by lazy {
        glslCode.findFunctionOrNull(entryPointName)
            ?.let { MatchLevel.Good }
            ?: MatchLevel.NoMatch
    }

    open fun additionalOutputPorts(): List<OutputPort> = emptyList()

    open fun adjustInputPorts(inputPorts: List<InputPort>): List<InputPort> = inputPorts
    open fun adjustOutputPorts(outputPorts: List<OutputPort>): List<OutputPort> = outputPorts

    private val wellKnownInputPortsById by lazy { wellKnownInputPorts.associateBy { it.id } }

    abstract fun findDeclaredInputPorts(): List<InputPort>

    open fun findInputPorts(): List<InputPort> {
        val proFormaInputPorts: List<InputPort> =
            implicitInputPorts.mapNotNull { glslCode.ifRefersTo(it)?.copy(isImplicit = true) }

        val declaredInputPorts = findDeclaredInputPorts()

        return adjustInputPorts(
            proFormaInputPorts +
                    declaredInputPorts +
                    findWellKnownInputPorts(declaredInputPorts.map { it.id }.toSet())
        )
    }

    open fun findOutputPorts(): List<OutputPort> {
        val entryPoint = findEntryPointOrNull()

        val entryPointReturn: OutputPort? =
            findEntryPointOutputPort(entryPoint, plugins)

        return adjustOutputPorts(
            listOfNotNull(entryPointReturn) +
                    (entryPoint?.getParamOutputPorts(plugins) ?: emptyList()) +
                    additionalOutputPorts()
        )
    }

    override fun analyze(existingShader: Shader?): ShaderAnalysis {
        return try {
            val inputPorts = findInputPorts()
            val outputPorts = findOutputPorts()
            val entryPoint = findEntryPointOrNull()
            Analysis(entryPoint, inputPorts, outputPorts, existingShader)
        } catch (e: GlslException) {
            ErrorsShaderAnalysis(glslCode.src, e, existingShader ?: createShader())
        }
    }

    inner class Analysis(
        override val entryPoint: GlslCode.GlslFunction?,
        override val inputPorts: List<InputPort>,
        override val outputPorts: List<OutputPort>,
        shader: Shader?
    ) : ShaderAnalysis {
        override val glslCode: GlslCode
            get() = this@BaseShaderAnalyzer.glslCode

        override val shaderDialect
            get() = this@BaseShaderAnalyzer.dialect

        override val isValid: Boolean =
            entryPoint != null &&
                    outputPorts.size == 1

        override val errors: List<GlslError> = arrayListOf<GlslError>().apply {
            operator fun GlslError.unaryPlus() = this@apply.add(this)

            if (entryPoint == null)
                +GlslError("No entry point \"$entryPointName\" among ${glslCode.functionNames.sorted()}")

            if (outputPorts.isEmpty())
                +GlslError("No output port found.")

            if (outputPorts.size > 1)
                +GlslError(
                    "Too many output ports found: " +
                            "${outputPorts.map { it.argSiteName }.sorted()}.", entryPoint?.lineNumber
                )

            inputPorts.forEach { inputPort ->
                if (inputPort.contentType.isUnknown())
                    +GlslError(
                        "Input port \"${inputPort.id}\" " +
                                "content type is \"${inputPort.contentType.id}\"", inputPort.glslArgSite?.lineNumber
                    )
            }

            outputPorts.forEach { outputPort ->
                if (outputPort.contentType.isUnknown())
                    +GlslError(
                        "Output port \"${outputPort.argSiteName}\" " +
                                "content type is \"${outputPort.contentType.id}\"",
                        outputPort.lineNumber ?: entryPoint?.lineNumber
                    )
            }
        }

        override val shader: Shader = shader
            ?: createShader()
    }

    private fun createShader() =
        Shader(findTitle() ?: "Untitled Shader", glslCode.src)

    abstract fun findEntryPointOutputPort(entryPoint: GlslCode.GlslFunction?, plugins: Plugins): OutputPort?

    abstract fun toInputPort(it: GlslCode.GlslFunction, plugins: Plugins): InputPort

    fun GlslCode.GlslArgSite.resolveInputPort(entryPoint: GlslCode.GlslFunction?, plugins: Plugins) =
        matchWellKnownPorts()
            ?: matchDefaults()
            ?: toInputPort(plugins, entryPoint)

    private fun GlslCode.GlslArgSite.matchWellKnownPorts() =
        wellKnownInputPortsById[name]?.copy(type = type, glslArgSite = this)

    private fun GlslCode.GlslArgSite.matchDefaults() =
        if (hint == null)
            defaultInputPortsByType[type]
                ?.copy(id = name, varName = name, glslArgSite = this)
        else null

    private fun GlslCode.ifRefersTo(inputPort: InputPort) =
        if (refersToGlobal(inputPort.id)) inputPort else null

    open fun findTitle(): String? =
        Regex("^// (.*)").find(glslCode.src)?.groupValues?.get(1)
            ?: glslCode.fileName?.let {
                Regex("\\.(fs|vs|glsl)$").replace(it, "")
            }

    fun findEntryPoint(): GlslCode.GlslFunction {
        return glslCode.findFunction(entryPointName)
    }

    fun findEntryPointOrNull(): GlslCode.GlslFunction? {
        return glslCode.findFunctionOrNull(entryPointName)
    }

    /**
     * Well-known input ports are uniforms that are automatically declared if they're used by a shader;
     * see if we're using any of them.
     */
    open fun findWellKnownInputPorts(declaredInputPorts: Set<String>): List<InputPort> {
        if (wellKnownInputPorts.isEmpty()) return emptyList()

        val symbolsMentionedInShader = glslCode.statements.flatMap { glslStatement ->
            glslStatement.uniqueTokens.filter { word ->
                wellKnownInputPortsById.containsKey(word)
            }.toList()
        }.toSet()

        return wellKnownInputPorts.filter { inputPort ->
            symbolsMentionedInShader.contains(inputPort.id) && !declaredInputPorts.contains(inputPort.id)
        }
    }
}
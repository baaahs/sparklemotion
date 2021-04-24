package baaahs.gl.shader.dialect

import baaahs.gl.glsl.*
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.plugin.Plugins
import baaahs.show.Shader

abstract class BaseShaderDialect(id: String) : ShaderDialect(id) {
    open val implicitInputPorts: List<InputPort> = emptyList()
    open val wellKnownInputPorts: List<InputPort> = emptyList()
    open val defaultInputPortsByType: Map<GlslType, InputPort> = emptyMap()
    abstract val entryPointName: String

    override fun matches(glslCode: GlslCode): MatchLevel {
        return glslCode.findFunctionOrNull(entryPointName)
            ?.let { MatchLevel.Good }
            ?: MatchLevel.NoMatch
    }

    open fun additionalOutputPorts(glslCode: GlslCode, plugins: Plugins): List<OutputPort> = emptyList()

    open fun adjustInputPorts(inputPorts: List<InputPort>): List<InputPort> = inputPorts
    open fun adjustOutputPorts(outputPorts: List<OutputPort>): List<OutputPort> = outputPorts

    private val wellKnownInputPortsById by lazy { wellKnownInputPorts.associateBy { it.id } }

    abstract fun findDeclaredInputPorts(
        glslCode: GlslCode,
        plugins: Plugins
    ): List<InputPort>

    open fun findInputPorts(glslCode: GlslCode, plugins: Plugins): List<InputPort> {
        val proFormaInputPorts: List<InputPort> =
            implicitInputPorts.mapNotNull { glslCode.ifRefersTo(it)?.copy(isImplicit = true) }

        val declaredInputPorts = findDeclaredInputPorts(glslCode, plugins)

        return adjustInputPorts(
            proFormaInputPorts +
                    declaredInputPorts +
                    findWellKnownInputPorts(glslCode, declaredInputPorts.map { it.id }.toSet())
        )
    }

    open fun findOutputPorts(glslCode: GlslCode, plugins: Plugins): List<OutputPort> {
        val entryPoint = findEntryPointOrNull(glslCode)

        val entryPointReturn: OutputPort? =
            findEntryPointOutputPort(entryPoint, plugins)

        return adjustOutputPorts(
            listOfNotNull(entryPointReturn) +
                    (entryPoint?.getParamOutputPorts(plugins) ?: emptyList()) +
                    additionalOutputPorts(glslCode, plugins)
        )
    }

    override fun analyze(glslCode: GlslCode, plugins: Plugins, shader: Shader?): ShaderAnalysis {
        try {
            val inputPorts = findInputPorts(glslCode, plugins)
            val outputPorts = findOutputPorts(glslCode, plugins)
            val entryPoint = findEntryPointOrNull(glslCode)
            return Analysis(glslCode, entryPoint, inputPorts, outputPorts, shader)
        } catch (e: GlslException) {
            return ErrorsShaderAnalysis(glslCode.src, e, shader ?: createShader(glslCode))
        }
    }

    inner class Analysis(
        override val glslCode: GlslCode,
        override val entryPoint: GlslCode.GlslFunction?,
        override val inputPorts: List<InputPort>,
        override val outputPorts: List<OutputPort>,
        shader: Shader?
    ) : ShaderAnalysis {
        override val shaderDialect = this@BaseShaderDialect

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
            ?: createShader(glslCode)
    }

    private fun createShader(glslCode: GlslCode) =
        Shader(findTitle(glslCode) ?: "Untitled Shader", glslCode.src)

    abstract fun findEntryPointOutputPort(entryPoint: GlslCode.GlslFunction?, plugins: Plugins): OutputPort?

    abstract fun toInputPort(it: GlslCode.GlslFunction, plugins: Plugins): InputPort

    fun GlslCode.GlslArgSite.resolveInputPort(entryPoint: GlslCode.GlslFunction?, plugins: Plugins) =
        (wellKnownInputPortsById[name]?.copy(type = type, glslArgSite = this)
            ?: defaultInputPortsByType[type]
                ?.copy(id = name, varName = name, glslArgSite = this)
            ?: toInputPort(plugins, entryPoint))

    private fun GlslCode.ifRefersTo(inputPort: InputPort) =
        if (refersToGlobal(inputPort.id)) inputPort else null

    open fun findTitle(glslCode: GlslCode): String? {
        return Regex("^// (.*)").find(glslCode.src)?.groupValues?.get(1)
    }

    fun findEntryPoint(glslCode: GlslCode): GlslCode.GlslFunction {
        return glslCode.findFunction(entryPointName)
    }

    fun findEntryPointOrNull(glslCode: GlslCode): GlslCode.GlslFunction? {
        return glslCode.findFunctionOrNull(entryPointName)
    }

    /**
     * Well-known input ports are uniforms that are automatically declared if they're used by a shader;
     * see if we're using any of them.
     */
    open fun findWellKnownInputPorts(glslCode: GlslCode, declaredInputPorts: Set<String>): List<InputPort> {
        if (wellKnownInputPorts.isEmpty()) return emptyList()

        val iVars = glslCode.functions.flatMap { glslFunction ->
            Regex("\\w+").findAll(glslFunction.fullText).map { it.value }.filter { word ->
                wellKnownInputPortsById.containsKey(word)
            }.toList()
        }.toSet()

        return wellKnownInputPorts.filter { inputPort ->
            iVars.contains(inputPort.id) && !declaredInputPorts.contains(inputPort.id)
        }
    }
}
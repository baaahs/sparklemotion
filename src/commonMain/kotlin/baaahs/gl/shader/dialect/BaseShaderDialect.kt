package baaahs.gl.shader.dialect

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslType
import baaahs.gl.glsl.ShaderAnalysis
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

    override fun analyze(glslCode: GlslCode, plugins: Plugins, shader: Shader?): ShaderAnalysis {
        val proFormaInputPorts: List<InputPort> =
            implicitInputPorts.mapNotNull { glslCode.ifRefersTo(it)?.copy(isImplicit = true) }

        val entryPoint = findEntryPointOrNull(glslCode)

        val entryPointParams =
            entryPoint?.params?.filter { it.isIn } ?: emptyList()

        val inputPorts = adjustInputPorts(
            proFormaInputPorts +
                    (glslCode.globalInputVars + entryPointParams).map {
                        it.resolveInputPort(entryPoint, plugins)
                    } +
                    glslCode.functions.filter { it.isAbstract }.map {
                        toInputPort(it, plugins)
                    } +
                    findMagicInputPorts(glslCode)
        )

        val entryPointReturn: OutputPort? =
            findEntryPointOutputPort(entryPoint, plugins)

        val outputPorts = adjustOutputPorts(
            listOfNotNull(entryPointReturn) +
                    (entryPoint?.getParamOutputPorts(plugins) ?: emptyList()) +
                    additionalOutputPorts(glslCode, plugins)
        )

        return object : ShaderAnalysis {
            override val glslCode = glslCode
            override val shaderDialect = this@BaseShaderDialect
            override val entryPoint = entryPoint
            override val inputPorts = inputPorts
            override val outputPorts = outputPorts

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
                    +GlslError("Too many output ports found: " +
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
                ?: Shader(
                    findTitle(glslCode) ?: "Untitled Shader",
                    glslCode.src
                )

        }
    }

    abstract fun findEntryPointOutputPort(entryPoint: GlslCode.GlslFunction?, plugins: Plugins): OutputPort?

    abstract fun toInputPort(it: GlslCode.GlslFunction, plugins: Plugins): InputPort

    private fun GlslCode.GlslArgSite.resolveInputPort(entryPoint: GlslCode.GlslFunction?, plugins: Plugins) =
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

    open fun findMagicInputPorts(glslCode: GlslCode): List<InputPort> = emptyList()
}
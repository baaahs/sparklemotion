package baaahs.gl.patch

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.show.live.LinkedShaderInstance
import baaahs.util.Logger

class ShaderComponent(
    val id: String,
    val index: Int,
    private val shaderInstance: LinkedShaderInstance,
    findUpstreamComponent: (ProgramNode) -> Component
) : Component {
    override val title: String get() = shaderInstance.shader.title
    private val prefix = "p$index"
    private val namespace = GlslCode.Namespace(prefix + "_" + id)
    private val portMap: Map<String, String>
    private val resultInReturnValue: Boolean
    private val resultVar: String

    init {
        val tmpPortMap = hashMapOf<String, String>()

        shaderInstance.incomingLinks.forEach { (toPortId, fromLink) ->
            val inputPort = shaderInstance.shader.findInputPort(toPortId)

            val upstreamComponent = findUpstreamComponent(fromLink)
            var expression = upstreamComponent.getExpression()
            val type = upstreamComponent.resultType
            if (inputPort.type != type) {
                expression = inputPort.contentType.adapt(expression, type)
            }
            tmpPortMap[toPortId] = expression
        }

        var usesReturnValue = false
        val outputPort = shaderInstance.shader.outputPort
        if (outputPort.isReturnValue()) {
            usesReturnValue = true
            resultVar = namespace.internalQualify("result")
        } else {
            resultVar = namespace.qualify(outputPort.id)
            tmpPortMap[outputPort.id] = resultVar
        }

        portMap = tmpPortMap
        resultInReturnValue = usesReturnValue
    }

    override val outputVar: String = resultVar
    override val resultType: GlslType
        get() = shaderInstance.shader.outputPort.dataType

    private val resolvedPortMap get() =
        portMap + mapOf(shaderInstance.shader.outputPort.id to outputVar)

    override fun appendStructs(buf: StringBuilder) {
        val openShader = shaderInstance.shader
        val portStructs = openShader.portStructs
        openShader.glslCode.structs.forEach { struct ->
            if (!portStructs.contains(struct.glslType)) {
                buf.append(struct.glslType.toGlsl(namespace, portStructs.map { it.name }.toSet()))
            }
        }
    }

    override fun appendDeclarations(buf: StringBuilder) {
        val openShader = shaderInstance.shader

        buf.append("// Shader: ", openShader.title, "; namespace: ", prefix, "\n")
        buf.append("// ", openShader.title, "\n")

        buf.append("\n")
        with(openShader.outputPort) {
            buf.append("${dataType.glslLiteral} $resultVar = ${contentType.initializer(dataType)};\n")
        }

        buf.append(openShader.toGlsl(namespace, resolvedPortMap), "\n")
    }

    override fun appendInvokeAndSet(buf: StringBuilder, prefix: String) {
        buf.append(prefix, "// Invoke ", title, "\n")
        val invocationGlsl = shaderInstance.shader.invocationGlsl(namespace, resultVar, resolvedPortMap)
        buf.append(prefix, invocationGlsl, ";\n")
        buf.append("\n")
    }

    override fun getExpression(): String {
        val outputPort = shaderInstance.shader.outputPort
        return if (outputPort.isReturnValue()) {
            resultVar
        } else {
            namespace.qualify(outputPort.id)
        }
    }

    companion object {
        private val logger = Logger<Component>()
    }
}
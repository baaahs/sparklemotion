package baaahs.gl.patch

import baaahs.gl.glsl.GlslCode
import baaahs.show.live.LiveShaderInstance
import baaahs.util.Logger

interface Component {
    val title: String
    val outputVar: String?

    fun appendDeclarations(buf: StringBuilder)
    fun appendInvokeAndSet(buf: StringBuilder, prefix: String)
}

class ShaderComponent(
    val id: String,
    val index: Int,
    private val shaderInstance: LiveShaderInstance,
    findUpstreamComponent: (ProgramNode) -> Component
): Component {
    override val title: String get() = shaderInstance.shader.title
    private val prefix = "p$index"
    private val namespace = GlslCode.Namespace(prefix + "_" + id)
    private val portMap: Map<String, String>
    private val resultInReturnValue: Boolean
    private val resultVar: String

    init {
        val tmpPortMap = hashMapOf<String, String>()

        shaderInstance.incomingLinks.forEach { (toPortId, fromLink) ->
            when (fromLink) {
                is LiveShaderInstance.ShaderOutLink -> {
                    val upstreamComponent = findUpstreamComponent(fromLink.shaderInstance)
                    upstreamComponent as ShaderComponent
                    val outputPort = fromLink.shaderInstance.shader.outputPort
                    tmpPortMap[toPortId] =
                        if (outputPort.isReturnValue()) {
                            upstreamComponent.resultVar
                        } else {
                            upstreamComponent.namespace.qualify(outputPort.id)
                        }
                }
                is LiveShaderInstance.DataSourceLink -> {
                    tmpPortMap[toPortId] = fromLink.dataSource.getVarName(fromLink.varName)
                }
                is LiveShaderInstance.ShaderChannelLink -> {
                    logger.warn { "Unexpected unresolved $fromLink for $toPortId" }
                }
                is LiveShaderInstance.ConstLink -> {
                    tmpPortMap[toPortId] = "(" + fromLink.glsl + ")"
                }
                is LiveShaderInstance.NoOpLink -> {
                }
            }
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

    private val resolvedPortMap get() =
        portMap + mapOf(shaderInstance.shader.outputPort.id to outputVar)

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

    companion object {
        private val logger = Logger<Component>()
    }
}
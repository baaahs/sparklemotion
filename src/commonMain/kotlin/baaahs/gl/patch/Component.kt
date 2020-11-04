package baaahs.gl.patch

import baaahs.gl.glsl.GlslCode
import baaahs.show.live.LiveShaderInstance
import baaahs.util.Logger

class Component(
    val index: Int,
    instanceNode: LinkedPatch.InstanceNode,
    findUpstreamComponent: (LiveShaderInstance) -> Component
) {
    private val shaderInstance = instanceNode.liveShaderInstance
    val title: String get() = shaderInstance.shader.title
    private val prefix = "p$index"
    private val namespace = GlslCode.Namespace(prefix + "_" + instanceNode.shaderShortName)
    private val portMap: Map<String, String>
    private val resultInReturnValue: Boolean
    private var resultVar: String

    init {
        val tmpPortMap = hashMapOf<String, String>()

        shaderInstance.incomingLinks.forEach { (toPortId, fromLink) ->
            when (fromLink) {
                is LiveShaderInstance.ShaderOutLink -> {
                    val upstreamComponent = findUpstreamComponent(fromLink.shaderInstance)
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

    var outputVar: String = resultVar
    private var resultRedirected = false

    private val resolvedPortMap get() =
        portMap + mapOf(shaderInstance.shader.outputPort.id to outputVar)

    fun redirectOutputTo(varName: String) {
        outputVar = varName
        resultRedirected = true
    }

    fun appendStructs(buf: StringBuilder) {
        shaderInstance.shader.glslCode.structs.forEach { struct ->
            // TODO: we really ought to namespace structs, but that's not straightforward because
            // multiple shaders might share a uniform input (e.g. ModelInfo).

//                val qualifiedName = namespace.qualify(struct.name)
//                val structText = struct.fullText.replace(struct.name, qualifiedName)
            val structText = struct.fullText
            buf.append(structText, "\n")
        }
    }

    fun appendDeclaratoryLines(buf: StringBuilder) {
        val openShader = shaderInstance.shader

        buf.append("// Shader: ", openShader.title, "; namespace: ", prefix, "\n")
        buf.append("// ", openShader.title, "\n")

        if (!resultRedirected) {
            buf.append("\n")
            with(openShader.outputPort) {
                buf.append("${dataType.glslLiteral} $resultVar = ${contentType.initializer(dataType)};\n")
            }
        }

        buf.append(openShader.toGlsl(namespace, resolvedPortMap), "\n")
    }

    fun invokeAndSetResultGlsl(): String {
        return shaderInstance.shader.invocationGlsl(namespace, resultVar, resolvedPortMap)
    }

    companion object {
        private val logger = Logger<Component>()
    }
}
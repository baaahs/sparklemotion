package baaahs.gl.patch

import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.ShaderSubstitutions
import baaahs.show.live.LinkedPatch

class ShaderComponent(
    val id: String,
    val index: Int,
    private val linkedPatch: LinkedPatch,
    private val findUpstreamComponent: (ProgramNode) -> Component
) : Component {
    override val title: String get() = linkedPatch.shader.title
    private val prefix = "p$index"
    private val namespace = GlslCode.Namespace(prefix + "_" + id)
    private val portMap: Map<String, GlslExpr>
    private val resultInReturnValue: Boolean
    private val resultVar: String

    init {
        val tmpPortMap = hashMapOf<String, GlslExpr>()

        linkedPatch.incomingLinks.forEach { (toPortId, fromLink) ->
            val inputPort = linkedPatch.shader.findInputPort(toPortId)

            val upstreamComponent = findUpstreamComponent(fromLink)
            var expression = upstreamComponent.getExpression(prefix)
            val type = upstreamComponent.resultType
            if (inputPort.type != type) {
                expression = inputPort.contentType.adapt(expression, type)
            }
            tmpPortMap[toPortId] = expression
        }

        var usesReturnValue = false
        val outputPort = linkedPatch.shader.outputPort
        if (outputPort.isReturnValue()) {
            usesReturnValue = true
            resultVar = namespace.internalQualify("result")
        } else {
            resultVar = namespace.qualify(outputPort.id)
            tmpPortMap[outputPort.id] = GlslExpr(resultVar)
        }

        portMap = tmpPortMap
        resultInReturnValue = usesReturnValue
    }

    override val outputVar: String = resultVar
    override val resultType: GlslType
        get() = linkedPatch.shader.outputPort.dataType

    override val invokeFromMain: Boolean
        get() = linkedPatch.injectedPorts.isEmpty()

    private val resolvedPortMap get() =
        portMap + mapOf(linkedPatch.shader.outputPort.id to GlslExpr(outputVar))

    private val substitutions = ShaderSubstitutions(linkedPatch.shader, namespace, resolvedPortMap)

    override fun appendStructs(buf: StringBuilder) {
        val openShader = linkedPatch.shader
        val portStructs = openShader.portStructs
        openShader.glslCode.structs.forEach { struct ->
            if (!portStructs.contains(struct.glslType)) {
                buf.append(struct.glslType.toGlsl(namespace, portStructs.map { it.name }.toSet()))
            }
        }
    }

    override fun appendDeclarations(buf: StringBuilder) {
        val openShader = linkedPatch.shader

        buf.append("// Shader: ", openShader.title, "; namespace: ", prefix, "\n")
        buf.append("// ", openShader.title, "\n")

        buf.append("\n")
        with(openShader.outputPort) {
            buf.append("${dataType.glslLiteral} $resultVar = ${contentType.initializer(dataType).s};\n")
        }

        appendInjectionCode(buf)

        buf.append(openShader.toGlsl(index, substitutions), "\n")
    }

    private fun appendInjectionCode(buf: StringBuilder) {
        linkedPatch.incomingLinks.forEach { (portId, link) ->
            val inputPort = linkedPatch.shader.findInputPort(portId)

            if (linkedPatch.injectedPorts.contains(portId)) {
                appendInjectionAssignment(buf, inputPort, portId)
            }

            if (inputPort.isAbstractFunction) {
                appendAbstractFunctionImpl(buf, inputPort, link)
            }
        }
    }

    private fun appendInjectionAssignment(
        buf: StringBuilder,
        inputPort: InputPort,
        portId: String
    ) {
        val contentType = inputPort.contentType
        val type = inputPort.contentType.glslType
        buf.append("${type.glslLiteral} ${prefix}_global_$portId = ${contentType.initializer(type).s};\n")
    }

    private fun appendAbstractFunctionImpl(
        buf: StringBuilder,
        inputPort: InputPort,
        link: ProgramNode
    ) {
        val fn = inputPort.glslArgSite as GlslCode.GlslFunction
        buf.append(fn.toGlsl(index) {
            if (it == fn.name) namespace.qualify(it) else it
        })
        val destComponent = findUpstreamComponent(link)
        buf.append(" {\n")
        destComponent.appendInvokeAndReturn(buf, inputPort)
        buf.append("}\n")
    }

    override fun appendInvokeAndSet(buf: StringBuilder, injectionParams: Map<String, ContentType>) {
        buf.append("    // Invoke ", title, "\n")

        injectionParams.forEach { (paramName, contentType) ->
            linkedPatch.shader.inputPorts.forEach { inputPort ->
                if (inputPort.contentType == contentType) {
                    buf.append("    ${prefix}_global_${inputPort.id} = $paramName;\n")
                }
            }
        }

        val invocationGlsl = linkedPatch.shader.invoker(namespace, resolvedPortMap).toGlsl(resultVar)
        buf.append("    ", invocationGlsl, ";\n")
        buf.append("\n")
    }

    override fun appendInvokeAndReturn(buf: StringBuilder, inputPort: InputPort) {
        appendInvokeAndSet(buf, inputPort.injectedData)
        buf.append("    return ", outputVar, ";\n")
    }

    override fun getExpression(prefix: String): GlslExpr {
        val outputPort = linkedPatch.shader.outputPort
        return if (outputPort.isReturnValue()) {
            resultVar
        } else {
            namespace.qualify(outputPort.id)
        }.let { GlslExpr(it) }
    }

    override fun getInit(): String? {
        return if (linkedPatch.shader.requiresInit) {
            StringBuilder().apply {
                append("    // Init ${title}.\n")
                append("    ${ShaderSubstitutions.namespacedInitFnName(namespace)}();\n")
                append("\n")
            }.toString()
        } else null
    }

    override fun toString(): String = "ShaderComponent(${prefix}_$id)"

}
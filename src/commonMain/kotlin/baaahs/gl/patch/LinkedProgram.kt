package baaahs.gl.patch

import baaahs.gl.glsl.GlslType
import baaahs.show.live.OpenPatch

class LinkedProgram(
    val rootNode: ProgramNode,
    private val components: List<Component>,
    val feedLinks: Set<OpenPatch.FeedLink>,
    val warnings: List<String>,
    internal val linkNodes: Map<ProgramNode, LinkNode> // For diagnostics only.
) {
    fun toGlsl(): String {
        val buf = StringBuilder()
        buf.append("#ifdef GL_ES\n")
        buf.append("precision mediump float;\n")
        buf.append("#endif\n")
        buf.append("\n")
        buf.append("// SparkleMotion-generated GLSL\n")
        buf.append("\n")
        with(rootNode.outputPort) {
            buf.append("layout(location = 0) out ${contentType.outputRepresentation.glslLiteral} sm_result;\n")
            buf.append("\n")

            if (contentType.glslType is GlslType.Struct) {
                buf.append(contentType.glslType.toGlsl(null, emptySet()))
            }
        }

        components.forEach { component ->
            component.appendStructs(buf)
        }

        components.forEach { component ->
            component.appendDeclarations(buf)
        }

        appendMain(buf)
        return buf.toString()
    }

    private fun appendMain(buf: StringBuilder) {
        buf.append("\n#line 10001\n")
        buf.append("void main() {\n")

        components.forEach { component ->
            component.getInit()?.let { buf.append(it) }
        }

        components.filter { it.invokeFromMain }.forEach { component ->
            component.appendInvokeAndSet(buf)
        }

        components.last().outputVar
            ?.let { outputVar ->
                buf.append("    sm_result = ")
                rootNode.outputPort.contentType.appendResultAsScalars(buf, outputVar)
                buf.append(";\n")
            }

        buf.append("}\n")
    }

    fun toFullGlsl(glslVersion: String): String {
        return "#version ${glslVersion}\n\n${toGlsl()}\n"
    }
}

package baaahs.gl.patch

import baaahs.show.live.OpenPatch

class LinkedProgram(
    val rootNode: ProgramNode,
    private val components: List<Component>,
    val feedLinks: Set<OpenPatch.FeedLink>,
    val warnings: List<String>,
    internal val linkNodes: Map<ProgramNode, LinkNode> // For diagnostics only.
) {
    fun toGlsl(): String {
        val outputContentType = rootNode.outputPort.contentType

        val buf = ProgramBuilder()
        buf.append("#ifdef GL_ES\n")
        // Mobile devices (e.g. iOS) only use ~10 bits for mediump, which isn't enough for
        // lots of stuff, e.g. fractional seconds in the time feed get truncated.
        buf.append("precision highp float;\n")
        buf.append("#endif\n")
        buf.append("\n")
        buf.append("// SparkleMotion-generated GLSL\n")
        buf.append("\n")
        buf.append("layout(location = 0) out ${outputContentType.outputRepresentation.glslLiteral} sm_result;\n")
        buf.append("\n")

        val outputStructs = outputContentType.glslType.collectTransitiveStructs()
        outputStructs.forEach { struct ->
            buf.append(struct.toGlsl(null, emptySet()))
        }

        val globalStructs = (outputStructs + components.flatMap { it.exportedStructs }).distinct()

        components.forEach { component ->
            component.appendStructs(buf, globalStructs)
        }

        components.forEach { component ->
            component.appendDeclarations(buf, globalStructs)
        }

        appendMain(buf)
        return buf.toString()
    }

    private fun appendMain(buf: ProgramBuilder) {
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

class ProgramBuilder(
    private val buf: StringBuilder = StringBuilder()
) : Appendable by buf {
    override fun toString(): String = buf.toString()
}
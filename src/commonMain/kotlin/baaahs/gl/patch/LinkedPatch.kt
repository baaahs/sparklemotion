package baaahs.gl.patch

import baaahs.gl.glsl.GlslCode
import baaahs.show.live.LiveShaderInstance.DataSourceLink

class LinkedPatch(
    val rootNode: ProgramNode,
    private val components: List<Component>,
    val dataSourceLinks: Set<DataSourceLink>,
    private var structs: Set<GlslCode.GlslStruct>,
    val warnings: List<String>
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
            buf.append("layout(location = 0) out ${dataType.glslLiteral} sm_result;\n")
        }
        buf.append("\n")

        structs.sortedBy { it.name }.forEach { struct ->
            buf.append(struct.fullText, "\n\n")
        }

        components.forEach { component ->
            component.appendDeclarations(buf)
        }

        buf.append("\n#line 10001\n")
        buf.append("void main() {\n")

        components.forEach { component ->
            component.appendInvokeAndSet(buf, "  ")
        }

        components.last().outputVar
            ?.let { buf.append("  sm_result = ", it, ";\n") }

        buf.append("}\n")
        return buf.toString()
    }

    fun toFullGlsl(glslVersion: String): String {
        return "#version ${glslVersion}\n\n${toGlsl()}\n"
    }
}

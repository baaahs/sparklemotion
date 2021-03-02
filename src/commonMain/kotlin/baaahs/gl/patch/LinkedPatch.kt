package baaahs.gl.patch

import baaahs.gl.glsl.GlslType
import baaahs.show.live.LiveShaderInstance.DataSourceLink

class LinkedPatch(
    val rootNode: ProgramNode,
    private val components: List<Component>,
    val dataSourceLinks: Set<DataSourceLink>,
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

        buf.append("\n#line 10001\n")
        buf.append("void main() {\n")

        components.forEach { component ->
            component.appendInvokeAndSet(buf)
        }

        components.last().outputVar
            ?.let { outputVar ->
                buf.append("    sm_result = ")
                rootNode.outputPort.contentType.appendResultAsScalars(buf, outputVar)
                buf.append(";\n")
            }

        buf.append("}\n")
        return buf.toString()
    }

    fun toFullGlsl(glslVersion: String): String {
        return "#version ${glslVersion}\n\n${toGlsl()}\n"
    }
}

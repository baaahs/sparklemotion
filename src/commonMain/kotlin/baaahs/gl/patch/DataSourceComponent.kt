package baaahs.gl.patch

import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType
import baaahs.gl.shader.InputPort
import baaahs.show.DataSource

class DataSourceComponent(
    val dataSource: DataSource,
    val varName: String,
    val dependencies: Map<String, Component>
) : Component {
    override val title: String
        get() = dataSource.title
    override val outputVar: String?
        get() = null
    override val resultType: GlslType
        get() = dataSource.getType()
    override val invokeFromMain: Boolean
        get() = true

    override fun appendStructs(buf: StringBuilder) {
        val glslType = dataSource.contentType.glslType
        if (glslType is GlslType.Struct) {
            buf.append(glslType.toGlsl(null, emptySet()))
        }
    }

    override fun appendDeclarations(buf: StringBuilder) {
        buf.append("// Data source: ", dataSource.title, "\n")
        dataSource.appendDeclaration(buf, varName)
        buf.append("\n")
    }

    override fun appendInvokeAndSet(buf: StringBuilder, injectionParams: Map<String, ContentType>) {
        dataSource.appendInvokeAndSet(buf, varName)
    }

    override fun appendInvokeAndReturn(buf: StringBuilder, inputPort: InputPort) {
        buf.append("    return ")
        dataSource.appendInvoke(buf, varName, inputPort)
        buf.append(";\n")
    }

    override fun getExpression(prefix: String): GlslExpr {
        return GlslExpr(dataSource.getVarName(varName))
    }

    override fun toString(): String {
        return "DataSourceComponent(dataSource=$dataSource, varName='$varName')"
    }
}
package baaahs.gl.patch

import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType
import baaahs.gl.shader.InputPort
import baaahs.show.Feed

class FeedComponent(
    val feed: Feed,
    val varName: String,
    val dependencies: Map<String, Component>
) : Component {
    override val title: String
        get() = feed.title
    override val outputVar: String?
        get() = null
    override val resultType: GlslType
        get() = feed.getType()
    override val invokeFromMain: Boolean
        get() = true

    override val exportedStructs: List<GlslType.Struct>
        get() = feed.contentType.glslType.collectTransitiveStructs()

    override fun appendStructs(buf: ProgramBuilder, globalStructs: List<GlslType.Struct>) {
        exportedStructs.forEach { struct ->
            buf.append(struct.toGlsl(null, emptySet()))
        }
    }

    override fun appendDeclarations(buf: ProgramBuilder, globalStructs: List<GlslType.Struct>) {
        buf.append("// Feed: ", feed.title, "\n")
        feed.appendDeclaration(buf, varName)
        buf.append("\n")
    }

    override fun appendInvokeAndSet(buf: ProgramBuilder, injectionParams: Map<String, ContentType>) {
        feed.appendInvokeAndSet(buf, varName)
    }

    override fun appendInvokeAndReturn(buf: ProgramBuilder, inputPort: InputPort) {
        buf.append("    return ")
        feed.appendInvoke(buf, varName, inputPort)
        buf.append(";\n")
    }

    override fun getExpression(prefix: String): GlslExpr {
        return GlslExpr(feed.getVarName(varName))
    }

    override fun toString(): String {
        return "FeedComponent(feed=$feed, varName='$varName')"
    }
}
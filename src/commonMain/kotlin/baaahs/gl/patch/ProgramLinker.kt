package baaahs.gl.patch

import baaahs.getBang
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.show.Shader
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch
import baaahs.show.mutable.ShowBuilder
import baaahs.util.CacheBuilder

class ProgramLinker(
    private val rootNode: ProgramNode,
    private val warnings: List<String> = emptyList()
) {
    private val linkNodes: MutableMap<ProgramNode, LinkNode> = mutableMapOf()
    private val dataSourceLinks = hashSetOf<OpenPatch.DataSourceLink>()
    private val showBuilder: ShowBuilder = ShowBuilder()
    private var curDepth = 0
    private val structs = hashSetOf<GlslCode.GlslStruct>()

    private val componentBuilder: CacheBuilder<ProgramNode, Component> = CacheBuilder {
        val instanceNode = linkNodes.getBang(it, "instance node")
        with(instanceNode) {
            programNode.buildComponent(id, index, this@ProgramLinker::findUpstreamComponent)
        }
    }

    private fun findUpstreamComponent(programNode: ProgramNode): Component =
        componentBuilder.getBang(programNode, "program node")

    fun visit(programNode: ProgramNode) {
        var newlyCreated = false
        linkNodes.getOrPut(programNode) {
            newlyCreated = true
            LinkNode(programNode, programNode.getNodeId(this))
        }.also {
            it.atDepth(curDepth)

            if (newlyCreated) {
                curDepth++
                try {
                    programNode.traverse(this)
                } finally {
                    curDepth--
                }
            }
        }
    }

    fun visit(dataSourceLink: OpenPatch.DataSourceLink) {
        dataSourceLinks.add(dataSourceLink)
        dataSourceLink.deps.forEach { (_, dependency) -> visit(dependency as ProgramNode) }
    }

    init { visit(rootNode) }

    fun buildLinkedProgram(): LinkedProgram {
        var pIndex = 0
        linkNodes.values
            .sortedWith(
                compareByDescending<LinkNode> { it.maxObservedDepth }
                    .thenBy { it.id }
            )
            .forEach { instanceNode ->
                instanceNode.index =
                    if (instanceNode.programNode is LinkedPatch)
                        pIndex++ else -1
            }

        val components = linkNodes.values
            .sortedWith(
                compareBy<LinkNode> { it.index }
                    .thenBy { it.id }
            )
            .map { componentBuilder[it.programNode] }

        return LinkedProgram(rootNode, components, dataSourceLinks, warnings)
    }

    fun visit(openShader: OpenShader) {
        // No-op for now. Previously we collected structs here.
    }

    fun idFor(shader: Shader): String = showBuilder.idFor(shader)
}

interface ProgramNode {
    val title: String
    val outputPort: OutputPort

    fun getNodeId(programLinker: ProgramLinker): String

    fun traverse(programLinker: ProgramLinker, depth: Int = 0)

    fun buildComponent(id: String, index: Int, findUpstreamComponent: (ProgramNode) -> Component): Component
}

data class DefaultValueNode(
    val contentType: ContentType
) : ProgramNode, Component {
    override val title: String = "Default value for $contentType"
    override val outputVar: String? = null
    override val resultType: GlslType get() = contentType.glslType
    override val outputPort: OutputPort = OutputPort(contentType)
    override val invokeFromMain: Boolean get() = true

    override fun getNodeId(programLinker: ProgramLinker): String = "n/a"

    override fun appendStructs(buf: StringBuilder) {
    }

    override fun appendDeclarations(buf: StringBuilder) {
    }

    override fun appendInvokeAndSet(buf: StringBuilder, injectionParams: Map<String, ContentType>) {
    }

    override fun getExpression(prefix: String): GlslExpr {
        return contentType.glslType.defaultInitializer
    }

    override fun traverse(programLinker: ProgramLinker, depth: Int) {
    }

    override fun buildComponent(id: String, index: Int, findUpstreamComponent: (ProgramNode) -> Component): Component {
        return this
    }
}

abstract class ExprNode : ProgramNode, Component {
    override val outputVar: String get() = TODO("not implemented")
    override val invokeFromMain: Boolean get() = true

    override fun getNodeId(programLinker: ProgramLinker): String = "n/a"

    override fun traverse(programLinker: ProgramLinker, depth: Int) {}

    override fun buildComponent(id: String, index: Int, findUpstreamComponent: (ProgramNode) -> Component): Component {
        return this
    }

    override fun appendStructs(buf: StringBuilder) {}
    override fun appendDeclarations(buf: StringBuilder) {}
    override fun appendInvokeAndSet(buf: StringBuilder, injectionParams: Map<String, ContentType>) {}
}

data class ConstNode(val glsl: String, override val outputPort: OutputPort) : ExprNode() {
    override val title: String get() = "const($glsl)"

    override val resultType: GlslType get() = outputPort.dataType

    override fun getExpression(prefix: String): GlslExpr = GlslExpr("($glsl)")

    override fun toString(): String = "ConstNode(glsl='$glsl', outputPort=$outputPort)"
}

class LinkNode(
    val programNode: ProgramNode,
    val id: String,
    var maxObservedDepth: Int = 0
) {
    var index: Int = -1

    fun atDepth(depth: Int) {
        if (depth > maxObservedDepth) maxObservedDepth = depth
    }
}
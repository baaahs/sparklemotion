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
            programNode.buildComponent(id, index, fullIndex, this@ProgramLinker::findUpstreamComponent)
        }
    }

    private fun findUpstreamComponent(programNode: ProgramNode): Component =
        componentBuilder.getBang(programNode, "program node")

    fun visit(programNode: ProgramNode) {
        linkNodes.getOrPut(programNode) {
            LinkNode(programNode, programNode.getNodeId(this))
        }.also {
            it.atDepth(curDepth)

            curDepth++
            try {
                programNode.traverse(this)
            } finally {
                curDepth--
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
        val allNodes = linkNodes.values
            .sortedWith(
                compareByDescending<LinkNode> { it.maxObservedDepth }
                    .thenBy { it.id }
            )

        // First pass: assign index numbers to first-class patches.
        allNodes.forEach { instanceNode ->
            val programNode = instanceNode.programNode
            if (programNode is LinkedPatch) {
                val modsNode = programNode.modsNode as? LinkedPatch
                if (modsNode == null) {
                    instanceNode.index = pIndex++
                }
            }
        }

        // Second pass: assign mod index numbers to patchmods.
        allNodes.forEach { instanceNode ->
            val programNode = instanceNode.programNode
            if (programNode is LinkedPatch) {
                val modsNode = programNode.modsNode as? LinkedPatch
                if (modsNode != null) {
                    val modsLinkNode = linkNodes.getBang(modsNode, "node")
                    instanceNode.index = modsLinkNode.index
                    instanceNode.modIndex = modsLinkNode.nextModIndex++
                }
            }
        }

        val components = linkNodes.values
            .sortedWith(
                compareBy<LinkNode> { it.index }
                    .thenBy { it.modIndex ?: Int.MAX_VALUE }
                    .thenBy { it.id }
            )
            .map { componentBuilder[it.programNode] }

        return LinkedProgram(rootNode, components, dataSourceLinks, warnings, linkNodes)
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

    fun traverse(programLinker: ProgramLinker)

    fun buildComponent(id: String, index: Int, prefix: String, findUpstreamComponent: (ProgramNode) -> Component): Component
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

    override fun traverse(programLinker: ProgramLinker) {
    }

    override fun buildComponent(
        id: String,
        index: Int,
        prefix: String,
        findUpstreamComponent: (ProgramNode) -> Component
    ): Component {
        return this
    }
}

abstract class ExprNode : ProgramNode, Component {
    override val outputVar: String get() = TODO("not implemented")
    override val invokeFromMain: Boolean get() = true

    override fun getNodeId(programLinker: ProgramLinker): String = "n/a"

    override fun traverse(programLinker: ProgramLinker) {}

    override fun buildComponent(
        id: String,
        index: Int,
        prefix: String,
        findUpstreamComponent: (ProgramNode) -> Component
    ): Component {
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
    var nextModIndex = 0
    var modIndex: Int? = null

    val fullIndex: String get() = if (modIndex == null) "p$index" else "p${index}m${modIndex}"

    fun atDepth(depth: Int) {
        if (depth > maxObservedDepth) maxObservedDepth = depth
    }

    override fun toString(): String {
        return "LinkNode(${programNode.title}, id='$id', maxObservedDepth=$maxObservedDepth, index=$index)"
    }
}

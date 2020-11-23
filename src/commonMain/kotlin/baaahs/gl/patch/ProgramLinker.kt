package baaahs.gl.patch

import baaahs.getBang
import baaahs.gl.glsl.GlslCode
import baaahs.gl.shader.OpenShader
import baaahs.show.Shader
import baaahs.show.live.LiveShaderInstance
import baaahs.show.mutable.ShowBuilder
import baaahs.util.CacheBuilder

class ProgramLinker(private val liveShaderInstance: LiveShaderInstance) {
    private val linkNodes: MutableMap<ProgramNode, LinkNode> = mutableMapOf()
    val dataSourceLinks = hashSetOf<LiveShaderInstance.DataSourceLink>()
    val showBuilder: ShowBuilder = ShowBuilder()
    private var curDepth = 0
    internal val structs = hashSetOf<GlslCode.GlslStruct>()

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
            programNode.asLinkNode(this)
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

    init { visit(liveShaderInstance) }

    fun buildLinkedPatch(): LinkedPatch {
        var pIndex = 0
        linkNodes.values
            .sortedWith(
                compareByDescending<LinkNode> { it.maxObservedDepth }
                    .thenBy { it.id }
            )
            .forEach { instanceNode ->
                instanceNode.index = when (instanceNode.programNode) {
                    is LiveShaderInstance.DataSourceLink -> -1
                    is LiveShaderInstance -> pIndex++
                    else -> error("huh?")
                }
            }

        val components = linkNodes.values
            .sortedWith(
                compareBy<LinkNode> { it.index }
                    .thenBy { it.id }
            )
            .map { componentBuilder[it.programNode] }

        return LinkedPatch(liveShaderInstance, components, dataSourceLinks, structs)
    }

    fun visit(openShader: OpenShader) {
        structs.addAll(openShader.glslCode.structs)
    }

    fun idFor(shader: Shader): String = showBuilder.idFor(shader)
}

interface ProgramNode {
    fun asLinkNode(programLinker: ProgramLinker): LinkNode

    fun traverse(programLinker: ProgramLinker, depth: Int = 0)

    fun buildComponent(id: String, index: Int, findUpstreamComponent: (ProgramNode) -> Component): Component
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

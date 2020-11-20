package baaahs.gl.patch

import baaahs.getBang
import baaahs.gl.glsl.GlslCode
import baaahs.gl.shader.OpenShader
import baaahs.show.live.LiveShaderInstance
import baaahs.show.mutable.ShowBuilder
import baaahs.util.CacheBuilder

class ProgramBuilder {
    private val instanceNodes: MutableMap<ProgramNode, LinkedPatch.InstanceNode> = mutableMapOf()
    val dataSourceLinks = hashSetOf<LiveShaderInstance.DataSourceLink>()
    val showBuilder: ShowBuilder = ShowBuilder()
    private var curDepth = 0
    internal val structs = hashSetOf<GlslCode.GlslStruct>()

    private val componentBuilder: CacheBuilder<ProgramNode, Component> = CacheBuilder {
        val instanceNode = instanceNodes.getBang(it, "instance node")
        with(instanceNode) {
            programNode.buildComponent(id, index, this@ProgramBuilder::findUpstreamComponent)
        }
    }

    private fun findUpstreamComponent(programNode: ProgramNode): Component =
        componentBuilder.getBang(programNode, "program node")


    fun visit(programNode: ProgramNode) {
        var newlyCreated = false
        instanceNodes.getOrPut(programNode) {
            newlyCreated = true
            programNode.asInstanceNode(this)
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

    fun calculateComponents(): List<Component> {
        var pIndex = 0
        instanceNodes.values
            .sortedWith(
                compareByDescending<LinkedPatch.InstanceNode> { it.maxObservedDepth }
                    .thenBy { it.id }
            )
            .forEach { instanceNode ->
                instanceNode.index = when (instanceNode.programNode) {
                    is LiveShaderInstance.DataSourceLink -> -1
                    is LiveShaderInstance -> pIndex++
                    else -> error("huh?")
                }
            }

        return instanceNodes.values
            .sortedWith(
                compareBy<LinkedPatch.InstanceNode> { it.index }
                    .thenBy { it.id }
            )
            .map { componentBuilder[it.programNode] }
    }

    fun visit(openShader: OpenShader) {
        structs.addAll(openShader.glslCode.structs)
    }
}

interface ProgramNode {
    fun asInstanceNode(programBuilder: ProgramBuilder): LinkedPatch.InstanceNode

    fun traverse(programBuilder: ProgramBuilder, depth: Int = 0)

    fun buildComponent(id: String, index: Int, findUpstreamComponent: (ProgramNode) -> Component): Component
}
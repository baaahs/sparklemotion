package baaahs.gl.patch

import baaahs.getBang
import baaahs.show.ShaderChannel
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.LiveShaderInstance.*
import baaahs.show.mutable.ShowBuilder
import baaahs.util.CacheBuilder
import baaahs.util.Logger
import kotlin.collections.component1
import kotlin.collections.component2

class LinkedPatch(val shaderInstance: LiveShaderInstance) {
    internal val dataSourceLinks: Set<DataSourceLink>
    private val componentBuilder: CacheBuilder<LiveShaderInstance, Component>
    private val components: List<Component>

    init {
        val instanceNodes = hashMapOf<LiveShaderInstance, InstanceNode>()
        val showBuilder = ShowBuilder()

        fun traverseLinks(liveShaderInstance: LiveShaderInstance, depth: Int = 0): Set<DataSourceLink> {
            instanceNodes.getOrPut(liveShaderInstance) {
                val shaderShortName = showBuilder.idFor(liveShaderInstance.shader.shader)
                InstanceNode(liveShaderInstance, shaderShortName)
            }.atDepth(depth)

            val dataSourceLinks = hashSetOf<DataSourceLink>()
            liveShaderInstance.incomingLinks.forEach { (_, link) ->
                when (link) {
                    is DataSourceLink -> dataSourceLinks.add(link)
                    is ShaderOutLink -> traverseLinks(link.shaderInstance, depth + 1)
                        .also { dataSourceLinks.addAll(it) }
                }
            }
            return dataSourceLinks
        }

        dataSourceLinks = traverseLinks(shaderInstance)

        instanceNodes.values
            .sortedWith(
                compareByDescending<InstanceNode> { it.maxDepth }
                    .thenBy { it.shaderShortName}
            )
            .forEachIndexed { index, instanceNode -> instanceNode.index = index }

        val componentsByChannel = hashMapOf<ShaderChannel, Component>()

        componentBuilder = CacheBuilder {
            val instanceNode = instanceNodes.getBang(it, "instance node")
            val index = instanceNode.index
            val component = Component(index, instanceNode, this@LinkedPatch::findUpstreamComponent)
            component
        }

        components = instanceNodes.values
            .sortedBy { it.index }
            .map { componentBuilder[it.liveShaderInstance] }

        componentsByChannel[ShaderChannel.Main]?.redirectOutputTo("sm_result")
    }

    private fun findUpstreamComponent(liveShaderInstance: LiveShaderInstance) =
        componentBuilder.getBang(liveShaderInstance, "shader")

    class InstanceNode(
        val liveShaderInstance: LiveShaderInstance,
        val shaderShortName: String,
        var maxDepth: Int = 0
    ) {
        var index: Int = -1

        fun atDepth(depth: Int) {
            if (depth > maxDepth) maxDepth = depth
        }
    }

    fun toGlsl(): String {
        val buf = StringBuilder()
        buf.append("#ifdef GL_ES\n")
        buf.append("precision mediump float;\n")
        buf.append("#endif\n")
        buf.append("\n")
        buf.append("// SparkleMotion-generated GLSL\n")
        buf.append("\n")
        with(shaderInstance.shader.outputPort) {
            buf.append("layout(location = 0) out ${dataType.glslLiteral} sm_result;\n")
        }
        buf.append("\n")

        components.forEach { component ->
            component.appendStructs(buf)
        }

        dataSourceLinks.sortedBy { it.varName }.forEach { (dataSource, varName) ->
            dataSource.appendDeclaration(buf, varName)
        }
        buf.append("\n")

        components.forEach { component ->
            component.appendDeclaratoryLines(buf)
        }

        buf.append("\n#line 10001\n")
        buf.append("void main() {\n")
        components.forEach { component ->
            buf.append("  ", component.invokeAndSetResultGlsl(), "; // ${component.title}\n")
        }
        buf.append("  sm_result = ", components.last().outputVar, ";\n")
        buf.append("}\n")
        return buf.toString()
    }

    fun toFullGlsl(glslVersion: String): String {
        return "#version ${glslVersion}\n\n${toGlsl()}\n"
    }

    companion object {
        private val logger = Logger("OpenPatch")
    }
}

package baaahs.gl.patch

import baaahs.fixtures.Fixture
import baaahs.getBang
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.Resolver
import baaahs.gl.render.RenderEngine
import baaahs.show.DataSource
import baaahs.show.ShaderChannel
import baaahs.show.Surfaces
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.LiveShaderInstance.*
import baaahs.show.mutable.ShowBuilder
import baaahs.util.CacheBuilder
import baaahs.util.Logger
import kotlin.collections.component1
import kotlin.collections.component2

class LinkedPatch(
    val shaderInstance: LiveShaderInstance,
    val surfaces: Surfaces
) {
    private val dataSourceLinks: Set<DataSourceLink>
    private val componentBuilder: CacheBuilder<LiveShaderInstance, Component>
    private val components: List<Component>

    init {
        val instanceNodes = hashMapOf<LiveShaderInstance, InstanceNode>()
        val showBuilder = ShowBuilder()

        fun LiveShaderInstance.traverseLinks(depth: Int = 0): Set<DataSourceLink> = traverse {
            println("  ".repeat(depth) + "Traverse ${shader.title}")
            instanceNodes.getOrPut(this) {
                val shaderShortName = showBuilder.idFor(shader.shader)
                InstanceNode(this, shaderShortName)
            }.atDepth(depth)

            val dataSourceLinks = hashSetOf<DataSourceLink>()
            incomingLinks.forEach { (portId, link) ->
                println("  ".repeat(depth) + " $portId ->")
                when (link) {
                    is DataSourceLink -> dataSourceLinks.add(link)
                    is ShaderOutLink ->
                        try {
                            link.shaderInstance.traverseLinks(depth + 1)
                                .also { dataSourceLinks.addAll(it) }
                        } catch (e: CircularGraphException) {
                            logger.error(e) { "Encountered circular graph link from ${shader.title}.$portId to ${shader.title}" }
                            throw e
                        }
                }
            }
            dataSourceLinks
        }

        dataSourceLinks = shaderInstance.traverseLinks()

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
            if (!dataSource.isImplicit())
                buf.append("uniform ${dataSource.getType().glslLiteral} ${dataSource.getVarName(varName)};\n")
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

    fun compile(renderEngine: RenderEngine, resolver: Resolver): GlslProgram =
        GlslProgram(renderEngine, this, resolver)

    fun createProgram(
        renderEngine: RenderEngine,
        dataFeeds: Map<DataSource, GlslProgram.DataFeed>
    ): GlslProgram {
        return compile(renderEngine) { _, dataSource -> dataFeeds.getBang(dataSource, "data feed") }
    }

    fun matches(fixture: Fixture): Boolean = this.surfaces.matches(fixture)

    fun bind(glslProgram: GlslProgram, resolver: Resolver): List<GlslProgram.Binding> {
        return dataSourceLinks.mapNotNull { (dataSource, id) ->
            if (dataSource.isImplicit()) return@mapNotNull null
            val dataFeed = resolver.invoke(id, dataSource)

            if (dataFeed != null) {
                val binding = dataFeed.bind(glslProgram)
                if (binding.isValid) binding else {
                    logger.debug { "unused uniform for $dataSource?" }
                    binding.release()
                    null
                }
            } else {
                logger.warn { "no UniformProvider bound for $dataSource" }
                null
            }
        }
    }

    companion object {
        private val logger = Logger("OpenPatch")
    }

    class CircularGraphException(shaderInstance: LiveShaderInstance) : Exception("Encountered circular graph at ${shaderInstance.shader.title}")
}

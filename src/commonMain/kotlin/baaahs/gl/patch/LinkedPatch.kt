package baaahs.gl.patch

import baaahs.fixtures.Fixture
import baaahs.getBang
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.Resolver
import baaahs.show.DataSource
import baaahs.show.ShaderChannel
import baaahs.show.Surfaces
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.LiveShaderInstance.*
import baaahs.show.mutable.ShowBuilder
import baaahs.util.Logger
import kotlin.collections.component1
import kotlin.collections.component2

class LinkedPatch(
    val shaderInstance: LiveShaderInstance,
    val surfaces: Surfaces
) {
    private val dataSourceLinks: Set<DataSourceLink>
    private val componentLookup: Map<LiveShaderInstance, Component>
    private val components = arrayListOf<Component>()

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

        val componentsByChannel = hashMapOf<ShaderChannel, Component>()
        componentLookup = instanceNodes.values
            .sortedWith(
                compareByDescending<InstanceNode> { it.maxDepth }
                    .thenBy { it.shaderShortName}
            )
            .mapIndexed { index, instanceNode ->
                val component = Component(index, instanceNode, this@LinkedPatch::findUpstreamComponent)
                components.add(component)
                instanceNode.liveShaderInstance to component
            }.associate { it }
        componentsByChannel[ShaderChannel.Main]?.redirectOutputTo("sm_result")
    }

    private fun findUpstreamComponent(liveShaderInstance: LiveShaderInstance) =
        componentLookup.getBang(liveShaderInstance, "shader")

    class InstanceNode(
        val liveShaderInstance: LiveShaderInstance,
        val shaderShortName: String,
        var maxDepth: Int = 0
    ) {
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

    fun compile(glContext: GlContext, resolver: Resolver): GlslProgram =
        GlslProgram(glContext, this, resolver)

    fun createProgram(
        glContext: GlContext,
        dataFeeds: Map<DataSource, GlslProgram.DataFeed>
    ): GlslProgram {
        return compile(glContext) { _, dataSource -> dataFeeds.getBang(dataSource, "data feed") }
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
}

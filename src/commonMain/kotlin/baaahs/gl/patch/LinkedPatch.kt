package baaahs.gl.patch

import baaahs.Logger
import baaahs.Surface
import baaahs.getBang
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.Resolver
import baaahs.show.ShaderChannel
import baaahs.show.Surfaces
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.LiveShaderInstance.*
import baaahs.show.mutable.ShowBuilder
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

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
                val component = Component(index, instanceNode)
                components.add(component)
                instanceNode.liveShaderInstance to component
            }.associate { it }
        componentsByChannel[ShaderChannel.Main]?.redirectOutputTo("sm_result")
    }


    class InstanceNode(
        val liveShaderInstance: LiveShaderInstance,
        val shaderShortName: String,
        var maxDepth: Int = 0
    ) {
        fun atDepth(depth: Int) {
            if (depth > maxDepth) maxDepth = depth
        }
    }

    inner class Component(
        val index: Int,
        val instanceNode: InstanceNode
    ) {
        private val shaderInstance = instanceNode.liveShaderInstance
        val title: String get() = shaderInstance.shader.title
        private val prefix = "p$index"
        private val namespace = GlslCode.Namespace(prefix + "_" + instanceNode.shaderShortName)
        private val portMap: Map<String, Lazy<String>>
        private val resultInReturnValue: Boolean
        private var resultVar: String

        init {
            val tmpPortMap = hashMapOf<String, Lazy<String>>()

            shaderInstance.incomingLinks.forEach { (toPortId, fromLink) ->
                when (fromLink) {
                    is ShaderOutLink -> {
                        tmpPortMap[toPortId] = lazy {
                            val fromComponent = componentLookup.getBang(fromLink.shaderInstance, "shader")
                            val outputPort = fromLink.shaderInstance.shader.outputPort
                            if (outputPort.isReturnValue()) {
                                fromComponent.resultVar
                            } else {
                                fromComponent.namespace.qualify(outputPort.id)
                            }
                        }
                    }
                    is DataSourceLink -> {
                        tmpPortMap[toPortId] = lazy {
                            fromLink.dataSource.getVarName(fromLink.varName)
                        }
                    }
                    is ShaderChannelLink -> {
                        logger.warn { "Unexpected unresolved $fromLink for $toPortId" }
                    }
                    is ConstLink -> {
                        tmpPortMap[toPortId] = lazy {
                            "(" + fromLink.glsl + ")"
                        }
                    }
                    is NoOpLink -> {
                    }
                }
            }

            var usesReturnValue = false
            val outputPort = shaderInstance.shader.outputPort
            if (outputPort.isReturnValue()) {
                usesReturnValue = true
                resultVar = namespace.internalQualify("result")
            } else {
                resultVar = namespace.qualify(outputPort.id)
                tmpPortMap[outputPort.id] = lazy { resultVar }
            }

            portMap = tmpPortMap
            resultInReturnValue = usesReturnValue
        }

        var outputVar: String = resultVar
        private var resultRedirected = false

        private val resolvedPortMap by lazy {
            portMap.mapValues { (_, v) -> v.value } +
                    mapOf(shaderInstance.shader.outputPort.id to outputVar)
        }

        fun redirectOutputTo(varName: String) {
            outputVar = varName
            resultRedirected = true
        }

        fun appendStructs(buf: StringBuilder) {
            shaderInstance.shader.glslCode.structs.forEach { struct ->
                // TODO: we really ought to namespace structs, but that's not straightforward because
                // multiple shaders might share a uniform input (e.g. ModelInfo).

//                val qualifiedName = namespace.qualify(struct.name)
//                val structText = struct.fullText.replace(struct.name, qualifiedName)
                val structText = struct.fullText
                buf.append(structText, "\n")
            }
        }

        fun appendDeclaratoryLines(buf: StringBuilder) {
            val openShader = shaderInstance.shader

            buf.append("// Shader: ", openShader.title, "; namespace: ", prefix, "\n")
            buf.append("// ", openShader.title, "\n")

            if (!resultRedirected) {
                buf.append("\n")
                with(openShader.outputPort) {
                    buf.append("${dataType.glslLiteral} $resultVar = ${contentType.initializer(dataType)};\n")
                }
            }

            buf.append(openShader.toGlsl(namespace, resolvedPortMap), "\n")
        }

        fun invokeAndSetResultGlsl(): String {
            return shaderInstance.shader.invocationGlsl(namespace, resultVar, resolvedPortMap)
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
                buf.append("uniform ${dataSource.getType()} ${dataSource.getVarName(varName)};\n")
        }
        buf.append("\n")

        components.forEach { component ->
            component.appendDeclaratoryLines(buf)
        }

        buf.append("\n#line -1\n")
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
        dataFeeds: Map<String, GlslProgram.DataFeed>
    ): GlslProgram {
        return compile(glContext) { id, dataSource -> dataFeeds.getBang(id, "data feed") }
    }

    fun matches(surface: Surface): Boolean = this.surfaces.matches(surface)

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

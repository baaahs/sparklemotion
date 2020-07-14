package baaahs.glshaders

import baaahs.Logger
import baaahs.Surface
import baaahs.getBang
import baaahs.glsl.GlslContext
import baaahs.show.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class OpenPatch {
//    constructor(shaders: List<OpenShader>, dataSources = List<DataSource>) {
//        components = shaders.associate {  }
//        dataSources = dataSources.
//    }

    constructor(patch: Patch, shadersById: Map<String, OpenShader>, dataSourcesById: Map<String, DataSource>) {
        val fromShader = hashMapOf<String, MutableList<Link>>()
        val toShader = hashMapOf<String, MutableList<Link>>()
        patch.links.forEach { link ->
            val (from, to) = link
            if (from is ShaderPortRef) fromShader.getOrPut(from.shaderId) { arrayListOf() }.add(link)
            if (to is ShaderPortRef) toShader.getOrPut(to.shaderId) { arrayListOf() }.add(link)
        }
        val shaderIds = patch.findShaderRefs().toList().sortedBy { shaderId ->
            val openShader = shadersById.getBang(shaderId, "shader")
            openShader.shaderType.sortOrder
        }
        components = shaderIds.mapIndexed { index, shaderId ->
            shaderId to Component(
                index, shadersById.getBang(shaderId, "shader"),
                toShader[shaderId] ?: emptyList(),
                fromShader[shaderId] ?: emptyList()
            )
        }.associate { it }
        dataSources = patch.findDataSourceRefs().associate {
            it.dataSourceId to dataSourcesById.getBang(it.dataSourceId, "datasource")
        }
        surfaces = patch.surfaces
    }

    private val components: Map<String, Component>
    private val dataSources: Map<String, DataSource>
    private val surfaces: Surfaces

    inner class Component(
        val index: Int,
        val openShader: OpenShader,
        incomingLinks: List<Link>,
        outgoingLinks: List<Link>
    ) {
        private val prefix = "p$index"
        private val namespace = GlslCode.Namespace(prefix)
        private val portMap: Map<String, Lazy<String>>
        private val outputsInUse: List<OutputPort>

        init {
            val tmpPortMap = hashMapOf<String, Lazy<String>>()
            val tmpOutgoingInUse = hashSetOf<String>()

            incomingLinks.forEach { (from, to) ->
                if (to is ShaderInPortRef) {
                    if (from is ShaderOutPortRef) {
                        tmpPortMap[to.portId] = lazy {
                            if (from.isReturnValue()) {
                                components.getBang(from.shaderId, "shader").resultVar
                            } else from.portId
                        }
                    } else if (from is DataSourceRef) {
                        tmpPortMap[to.portId] = lazy {
                            dataSources.getBang(from.dataSourceId, "datasource").getVarName(from.dataSourceId)
                        }
                    }
                }
            }

            outgoingLinks.forEach { (from, to) ->
                if (from is ShaderOutPortRef) {
                    tmpOutgoingInUse.add(from.portId)
                }

                println("outgoing link $from -> $to")
                if (to is OutputPortRef && from is ShaderOutPortRef) {
                    tmpPortMap[from.portId] = lazy { to.portId }
                }
            }

            portMap = tmpPortMap
            outputsInUse = openShader.outputPorts.filter { tmpOutgoingInUse.contains(it.id) }
        }

        private val saveResult = outgoingLinks.any { (from, _) -> from is ShaderOutPortRef && from.isReturnValue() }
        private val resultVar = namespace.internalQualify("result")
        private val resolvedPortMap by lazy { portMap.mapValues { (_, v) -> v.value } }

        fun appendStructs(buf: StringBuilder) {
            openShader.glslCode.structs.forEach { struct ->
                // TODO: we really ought to namespace structs, but that's not straightforward because
                // multiple shaders might share a uniform input (e.g. ModelInfo).

//                val qualifiedName = namespace.qualify(struct.name)
//                val structText = struct.fullText.replace(struct.name, qualifiedName)
                val structText = struct.fullText
                buf.append(structText, "\n")
            }
        }

        fun appendDeclaratoryLines(buf: StringBuilder) {
            buf.append("// Shader: ", openShader.title, "; namespace: ", prefix, "\n")
            buf.append("// ", openShader.title, "\n")

            outputsInUse.forEach { outputPort ->
                if (!outputPort.isImplicit) {
                    buf.append("${outputPort.toGlsl(namespace)};\n")
                }
            }

//            if (saveResult) {
//                buf.append("\n")
//                buf.append("${openShader.entryPoint.returnType} $resultVar;\n")
//            }

            buf.append(openShader.toGlsl(namespace, resolvedPortMap), "\n")
        }

        fun appendMainLines(buf: StringBuilder) {
            buf.append("  ")
            if (saveResult) {
                buf.append(resultVar, " = ")
            }
            buf.append(openShader.invocationGlsl(namespace, resolvedPortMap))
            buf.append(";\n")
        }
    }

    fun toGlsl(): String {
        val buf = StringBuilder()
        buf.append("#ifdef GL_ES\n")
        buf.append("precision mediump float;\n")
        buf.append("#endif\n")
        buf.append("\n")
        buf.append("// SparkleMotion generated GLSL\n")
        buf.append("\n")
        buf.append("layout(location = 0) out vec4 sm_pixelColor;\n")
        buf.append("\n")

        components.values.forEach { component ->
            component.appendStructs(buf)
        }

        dataSources.forEach { (id, dataSource) ->
            if (!dataSource.isImplicit())
                buf.append("uniform ${dataSource.getType()} ${dataSource.getVarName(id)};\n")
        }
        buf.append("\n")

        components.values.forEach { component ->
            component.appendDeclaratoryLines(buf)
        }

        buf.append("\n#line -1\n")
        buf.append("void main() {\n")
        components.values.forEach { component ->
            component.appendMainLines(buf)
        }
        buf.append("}\n")
        return buf.toString()
    }

    fun compile(glslContext: GlslContext, resolver: Resolver): GlslProgram =
        GlslProgram(glslContext, this, resolver)

    fun createProgram(
        glslContext: GlslContext,
        dataFeeds: Map<String, GlslProgram.DataFeed>
    ): GlslProgram {
        return compile(glslContext) { id, dataSource -> dataFeeds.getBang(id, "data feed") }
    }

    fun matches(surface: Surface): Boolean = this.surfaces.matches(surface)

    fun bind(glslProgram: GlslProgram, resolver: Resolver): List<GlslProgram.Binding> {
        return dataSources.mapNotNull { (id, dataSource) ->
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

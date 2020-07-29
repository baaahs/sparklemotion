package baaahs.glshaders

import baaahs.Logger
import baaahs.Surface
import baaahs.getBang
import baaahs.glsl.GlslContext
import baaahs.show.*
import baaahs.show.live.LiveShaderInstance
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class OpenPatch(
    patch: Patch,
    allShaderInstancesById: Map<String, LiveShaderInstance>,
    dataSourcesById: Map<String, DataSource>
) {
    private val components: Map<String, Component>
    private val dataSources: Map<String, DataSource>
    private val surfaces: Surfaces

    init {
        val componentsByRole = hashMapOf<ShaderRole, Component>()
        val dataSourceRefs = hashSetOf<DataSourceRef>()
        components = patch.shaderInstanceIds.mapIndexed { index, id ->
            val shaderInstance = allShaderInstancesById.getBang(id, "shader instance")
            dataSourceRefs.addAll(shaderInstance.findDataSourceRefs())
            val component = Component(index, shaderInstance)
            shaderInstance.role?.let { componentsByRole[it] = component }
            id to component
        }.associate { it }
        componentsByRole[ShaderRole.Paint]?.redirectOutputTo("sm_pixelColor")
        dataSources = dataSourceRefs.associate {
            it.dataSourceId to dataSourcesById.getBang(it.dataSourceId, "datasource")
        }
        surfaces = patch.surfaces
    }

    inner class Component(
        val index: Int,
        val shaderInstance: LiveShaderInstance
    ) {
        private val prefix = "p$index"
        private val namespace = GlslCode.Namespace(prefix)
        private val portMap: Map<String, Lazy<String>>
        private val resultInReturnValue: Boolean
        private var resultVar: String

        init {
            val tmpPortMap = hashMapOf<String, Lazy<String>>()

            shaderInstance.incomingLinks.forEach { (toPortId, fromPortRef) ->
                if (fromPortRef is ShaderOutPortRef) {
                    tmpPortMap[toPortId] = lazy {
                        val fromComponent = components.getBang(fromPortRef.shaderInstanceId, "shader")
                        if (fromPortRef.isReturnValue()) {
                            fromComponent.resultVar
                        } else {
                            fromComponent.namespace.qualify(fromPortRef.portId)
                        }
                    }
                } else if (fromPortRef is DataSourceRef) {
                    tmpPortMap[toPortId] = lazy {
                        dataSources.getBang(fromPortRef.dataSourceId, "datasource").getVarName(fromPortRef.dataSourceId)
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

        private var outputVar: String = resultVar

        private val resolvedPortMap by lazy {
            portMap.mapValues { (_, v) -> v.value } +
                    mapOf(shaderInstance.shader.outputPort.id to outputVar)
        }

        fun redirectOutputTo(varName: String) {
            outputVar = varName
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

            if (resultInReturnValue) {
                buf.append("\n")
                buf.append("${openShader.entryPoint.returnType} $resultVar;\n")
            }

            buf.append(openShader.toGlsl(namespace, resolvedPortMap), "\n")
        }

        fun appendMainLines(buf: StringBuilder) {
            val openShader = shaderInstance.shader

            buf.append("  ")
            if (resultInReturnValue) {
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

        dataSources.entries.sortedBy { it.key }.forEach { (id, dataSource) ->
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

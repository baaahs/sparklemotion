package baaahs.glshaders

import baaahs.glsl.GlslContext
import baaahs.ports.*
import baaahs.show.DataSource
import baaahs.unknown
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class Patch(
    shaderFragments: Map<String, ShaderFragment>,
    val dataSources: List<DataSource>,
    val links: List<Link>
) {
    val components: Map<String, Component>

    init {
        val fromById = hashMapOf<String, ArrayList<Link>>()
        val toById = hashMapOf<String, ArrayList<Link>>()

        links.forEach { link ->
            val (from, to) = link
            if (from is ShaderPortRef) fromById.getOrPut(from.shaderId) { arrayListOf() }.add(link)
            if (to is ShaderPortRef) toById.getOrPut(to.shaderId) { arrayListOf() }.add(link)
        }
        val shaderIdsInUse = fromById.keys + toById.keys

        components = shaderFragments.entries.mapIndexedNotNull { i, (shaderId, shaderFragment) ->
            if (shaderIdsInUse.contains(shaderId)) {
                Component(
                    i, shaderId, shaderFragment,
                    toById[shaderId] ?: emptyList(),
                    fromById[shaderId] ?: emptyList()
                )
            } else null
        }.associateBy { it.shaderId }
    }

    val dataSourceRefs: List<DataSourceRef>
        get() = links.map { it.from }.filterIsInstance<DataSourceRef>()

    inner class Component(
        val index: Int,
        val shaderId: String,
        val shaderFragment: ShaderFragment,
        incomingLinks: List<Link>,
        outgoingLinks: List<Link>
    ) {
        private val prefix = "p$index"
        private val namespace = GlslCode.Namespace(prefix)
        private val portMap: Map<String, () -> String>

        init {
            val dataSourcesById = dataSources.associateBy { it.id }
            val tmpPortMap = hashMapOf<String, () -> String>()

            incomingLinks.forEach { (from, to) ->
                if (to is ShaderInPortRef) {
                    if (from is ShaderOutPortRef) {
                        tmpPortMap[to.portName] =
                            {
                                components[from.shaderId]?.resultVar
                                    ?: error(unknown("shader", from.shaderId, components.keys))
                            }
                    } else if (from is DataSourceRef) {
                        tmpPortMap[to.portName] =
                            {
                                val dataSource = dataSourcesById[from.id]
                                    ?: error(unknown("datasource", from.id, dataSourcesById.keys))
                                dataSource.getVarName()
                            }
                    }
                }
            }

            tmpPortMap["gl_FragColor"] = { "sm_pixelColor" }
            portMap = tmpPortMap
        }

        val saveResult = outgoingLinks.any { (from, _) -> from is ShaderOutPortRef }
        val resultVar = namespace.internalQualify("result")
        val resolvedPortMap by lazy { portMap.mapValues { (_, v) -> v() } }

        fun appendDeclaratoryLines(buf: StringBuilder) {
            buf.append("// Shader ID: ", shaderId, "; namespace: ", prefix, "\n")
            buf.append("// ", shaderFragment.title, "\n")

            if (saveResult) {
                buf.append("\n")
                buf.append("${shaderFragment.entryPoint.returnType} $resultVar;\n")
            }

            buf.append(shaderFragment.toGlsl(namespace, resolvedPortMap), "\n")
        }

        fun appendMainLines(buf: StringBuilder) {
            buf.append("  ")
            if (saveResult) { buf.append(resultVar, " = ") }
            buf.append(shaderFragment.invocationGlsl(namespace, resolvedPortMap))
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

        dataSources.forEach {
            if (!it.isImplicit())
                buf.append("uniform ${it.getType()} ${it.getVarName()};\n")
        }
        buf.append("\n")

        components.values.forEach { component ->
            component.appendDeclaratoryLines(buf)
        }

        buf.append("\n#line 10001\n")
        buf.append("void main() {\n")
        components.values.forEach { component ->
            component.appendMainLines(buf)
        }
        buf.append("}\n")
        return buf.toString()
    }

    fun compile(glslContext: GlslContext, resolver: Resolver): GlslProgram =
        GlslProgram(glslContext, this, resolver)
}

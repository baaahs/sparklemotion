package baaahs.show.live

import baaahs.getBang
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.*
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.show.*
import baaahs.util.CacheBuilder
import baaahs.util.Logger

class LiveShaderInstance(
    val shader: OpenShader,
    val incomingLinks: Map<String, Link>,
    val shaderChannel: ShaderChannel?,
    val priority: Float
) {
    fun release() {
        shader.release()
    }

    fun finalResolve(resolver: PortDiagram.Resolver): ProgramNode {
        val resolvedIncomingLinks = incomingLinks.mapValues { (portId, link) ->
            val inputPort = shader.findInputPort(portId)
            resolver.resolveLink(inputPort, link)
        }

        return LinkedShaderInstance(shader, resolvedIncomingLinks, shaderChannel, priority)
    }

    override fun toString(): String {
        return "LiveShaderInstance(shader=${shader.title}, incomingLinks=${incomingLinks.keys.sorted()}, shaderChannel=$shaderChannel)"
    }


    interface Link {
        fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode
    }

    data class ShaderOutLink(val shaderInstance: LiveShaderInstance) : Link {
        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode {
            return shaderInstance.finalResolve(resolver)
        }
    }

    data class DataSourceLink(val dataSource: DataSource, val varName: String) : Link, ProgramNode {
        override val title: String get() = dataSource.title
        override val outputPort: OutputPort get() = OutputPort(dataSource.contentType)

        override fun getNodeId(programLinker: ProgramLinker): String = varName

        override fun traverse(programLinker: ProgramLinker, depth: Int) {
            programLinker.visit(this)
        }

        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode =
            resolver.resolveChannel(
                inputPort.copy(contentType = dataSource.contentType),
                ShaderChannel(varName)
            )

        override fun buildComponent(id: String, index: Int, findUpstreamComponent: (ProgramNode) -> Component): Component {
            return object : Component {
                override val title: String
                    get() = dataSource.title
                override val outputVar: String?
                    get() = null

                override fun appendDeclarations(buf: StringBuilder) {
                    if (!dataSource.isImplicit()) {
                        buf.append("// Data source: ", dataSource.title, "\n")
                        dataSource.appendDeclaration(buf, varName)
                        buf.append("\n")
                    }
                }

                override fun appendInvokeAndSet(buf: StringBuilder, prefix: String) {
                }

                override fun getExpression(): String {
                    return dataSource.getVarName(varName)
                }
            }
        }
    }

    data class ShaderChannelLink(val shaderChannel: ShaderChannel) : Link {
        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode =
            resolver.resolveChannel(inputPort, shaderChannel)
    }

    data class ConstLink(val glsl: String, val type: GlslType) : Link {
        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode {
            return ConstNode(glsl, OutputPort(ContentType.Unknown, dataType = type))
        }
    }
}

fun DataSource.link(varName: String) = LiveShaderInstance.DataSourceLink(this, varName)

class ShaderInstanceResolver(
    val openShaders: CacheBuilder<String, OpenShader>,
    val shaderInstances: Map<String, ShaderInstance>,
    val dataSources: Map<String, DataSource>
) {
    private val liveShaderInstances = hashMapOf<String, LiveShaderInstance>()

    init {
        shaderInstances.keys.forEach { shaderInstanceId ->
            resolve(shaderInstanceId)
        }
    }

    private fun findDataSource(id: String) = dataSources.getBang(id, "data source")
    private fun findShader(id: String): OpenShader = openShaders.getBang(id, "open shader")
    private fun findShaderInstance(id: String): ShaderInstance = shaderInstances.getBang(id, "shader instance")

    private fun resolve(id: String): LiveShaderInstance {
        liveShaderInstances[id]?.let { return it }

        val shaderInstance = findShaderInstance(id)
        val shader = findShader(shaderInstance.shaderId)
        val knownInputPorts = shader.inputPorts.associateBy { it.id }

        val links = shaderInstance.incomingLinks
            .filterKeys { portId ->
                knownInputPorts.contains(portId).also { containsKey ->
                    if (!containsKey) logger.warn { "Unknown port mapping \"$portId\" for shader \"${shader.title}\"" }
                }
            }
            .mapValues { (_, portRef) ->
                when (portRef) {
                    is ShaderOutPortRef -> LiveShaderInstance.ShaderOutLink(resolve(portRef.shaderInstanceId))
                    is DataSourceRef -> findDataSource(portRef.dataSourceId).link(portRef.dataSourceId)
                    is ShaderChannelRef -> LiveShaderInstance.ShaderChannelLink(portRef.shaderChannel)
                    is OutputPortRef -> TODO()
                    is ConstPortRef -> LiveShaderInstance.ConstLink(portRef.glsl, GlslType.from(portRef.type))
                }
            }

        return LiveShaderInstance(shader, links, shaderInstance.shaderChannel, shaderInstance.priority).also {
            liveShaderInstances[id] = it
        }
    }

    fun getResolvedShaderInstances() = liveShaderInstances

    companion object {
        private val logger = Logger("ShaderInstanceResolver")
    }
}
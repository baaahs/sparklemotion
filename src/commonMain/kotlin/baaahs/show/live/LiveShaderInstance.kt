package baaahs.show.live

import baaahs.Severity
import baaahs.ShowProblem
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
    val shaderChannel: ShaderChannel,
    val priority: Float = 0f,
    val extraLinks: Set<String> = emptySet(),
    val missingLinks: Set<String> = emptySet()
) {
    val title get() = shader.title

    val isFilter: Boolean get() = with (shader) {
        inputPorts.any {
            it.contentType == outputPort.contentType && incomingLinks[it.id]?.let { link ->
                link is ShaderChannelLink && link.shaderChannel == shaderChannel
            } == true
        }
    }

    val problems: List<ShowProblem> get() =
        arrayListOf<ShowProblem>().apply {
            if (extraLinks.isNotEmpty()) {
                add(
                    ShowProblem(
                        "Extra incoming links on shader \"$title\"",
                        "Unknown ports: ${extraLinks.sorted().joinToString(", ")}",
                        severity = Severity.WARN
                    )
                )
            }
            if (missingLinks.isNotEmpty()) {
                add(
                    ShowProblem(
                        "Missing incoming links on shader \"$title",
                        "No link for ports: ${missingLinks.sorted().joinToString(", ")}",
                        severity = Severity.ERROR
                    )
                )
            }
            if (shader.outputPort.contentType.isUnknown()) {
                add(
                    ShowProblem(
                    "Result content type is unknown for shader \"$title\".", severity = Severity.ERROR
                    )
                )
            }
            if (shader.errors.isNotEmpty()) {
                add(
                    ShowProblem(
                        "GLSL errors in shader \"$title\".", severity = Severity.ERROR
                    )
                )
            }
        }

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

        override fun buildComponent(
            id: String,
            index: Int,
            findUpstreamComponent: (ProgramNode) -> Component
        ): Component {
            return DataSourceComponent(dataSource, varName)
        }
    }

    data class ShaderChannelLink(val shaderChannel: ShaderChannel) : Link {
        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode =
            resolver.resolveChannel(inputPort, shaderChannel)
    }

    data class ConstLink(val glsl: String, val type: GlslType) : Link {
        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode {
            return ConstNode(glsl, OutputPort(ContentType.unknown(type), dataType = type))
        }
    }
}

fun DataSource.link(varName: String) = LiveShaderInstance.DataSourceLink(this, varName)

class ShaderInstanceResolver(
    private val openShaders: CacheBuilder<String, OpenShader>,
    private val shaderInstances: Map<String, ShaderInstance>,
    private val dataSources: Map<String, DataSource>
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
                    if (!containsKey)
                        logger.warn {
                            "Unknown port mapping \"$portId\" for shader \"${shader.title}\" " +
                                    "(have ${knownInputPorts.keys.sorted()})"
                        }
                }
            }
            .mapValues { (_, portRef) ->
                when (portRef) {
                    is DataSourceRef -> findDataSource(portRef.dataSourceId).link(portRef.dataSourceId)
                    is ShaderChannelRef -> LiveShaderInstance.ShaderChannelLink(portRef.shaderChannel)
                    is OutputPortRef -> TODO()
                    is ConstPortRef -> LiveShaderInstance.ConstLink(portRef.glsl, GlslType.from(portRef.type))
                }
            }

        val ports = shader.inputPorts.map { it.id }
        val extraLinks = shaderInstance.incomingLinks.keys - ports
        val missingLinks = ports - shaderInstance.incomingLinks.keys

        return LiveShaderInstance(
            shader,
            links,
            shaderInstance.shaderChannel,
            shaderInstance.priority,
            extraLinks = extraLinks,
            missingLinks = missingLinks.toSet()
        ).also {
            liveShaderInstances[id] = it
        }
    }

    fun getResolvedShaderInstances() = liveShaderInstances

    companion object {
        private val logger = Logger("ShaderInstanceResolver")
    }
}
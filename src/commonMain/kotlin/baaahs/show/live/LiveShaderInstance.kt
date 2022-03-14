package baaahs.show.live

import baaahs.getBang
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.*
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.show.*
import baaahs.sm.webapi.Problem
import baaahs.sm.webapi.Severity
import baaahs.util.CacheBuilder
import baaahs.util.Logger

class LiveShaderInstance(
    val shader: OpenShader,
    val incomingLinks: Map<String, Link>,
    val shaderChannel: ShaderChannel,
    val priority: Float = 0f,
    val extraLinks: Set<String> = emptySet(),
    val missingLinks: Set<String> = emptySet(),
    val injectedPorts: Set<String> = emptySet()
) {
    val title get() = shader.title

    val isFilter: Boolean
        get() = with(shader) {
            inputPorts.any {
                it.contentType == outputPort.contentType && incomingLinks[it.id]?.let { link ->
                    link is ShaderChannelLink && link.shaderChannel == shaderChannel
                } == true
            }
        }

    val problems: List<Problem>
        get() =
            arrayListOf<Problem>().apply {
                incomingLinks
                    .forEach { (_, link) ->
                        val dataSourceLink = link as? DataSourceLink
                        val unknownDataSource = dataSourceLink?.dataSource as? UnknownDataSource
                        unknownDataSource?.let {
                            add(
                                Problem(
                                    "Unresolved data source for shader \"$title\".",
                                    it.errorMessage, severity = Severity.WARN
                                )
                            )
                        }
                    }

                if (extraLinks.isNotEmpty()) {
                    add(
                        Problem(
                            "Extra incoming links on shader \"$title\"",
                            "Unknown ports: ${extraLinks.sorted().joinToString(", ")}",
                            severity = Severity.WARN
                        )
                    )
                }

                if (missingLinks.isNotEmpty()) {
                    add(
                        Problem(
                            "Missing incoming links on shader \"$title",
                            "No link for ports: ${missingLinks.sorted().joinToString(", ")}",
                            severity = Severity.ERROR
                        )
                    )
                }

                if (shader.outputPort.contentType.isUnknown()) {
                    add(
                        Problem(
                            "Result content type is unknown for shader \"$title\".", severity = Severity.ERROR
                        )
                    )
                }

                if (shader.errors.isNotEmpty()) {
                    add(
                        Problem(
                            "GLSL errors in shader \"$title\".", severity = Severity.ERROR
                        )
                    )
                }
            }

    fun release() {
        shader.disuse()
    }

    fun maybeWithInjectedData(injectedData: Set<ContentType>): LiveShaderInstance {
        val injectedPorts = mutableSetOf<String>()

        val newLinks = incomingLinks.mapValues { (portId, link) ->
            val inputPort = shader.findInputPort(portId)
            if (injectedData.contains(inputPort.contentType)) {
                injectedPorts.add(inputPort.id)
                InjectedDataLink()
            } else link
        }

        return if (injectedPorts.isNotEmpty()) {
            LiveShaderInstance(shader, newLinks, shaderChannel, priority, extraLinks, missingLinks, injectedPorts)
        } else this
    }

    fun finalResolve(resolver: PortDiagram.Resolver): ProgramNode {
        val resolvedIncomingLinks = incomingLinks.mapValues { (portId, link) ->
            val inputPort = shader.findInputPort(portId)

            if (inputPort.injectedData.isNotEmpty()) {
                println("${inputPort.title} injects: ${inputPort.injectedData}")
                val fn = inputPort.glslArgSite as? GlslCode.GlslFunction
                resolver.resolveLink(inputPort, link)
            } else {
                resolver.resolveLink(inputPort, link)
            }
        }

        return LinkedShaderInstance(shader, resolvedIncomingLinks, shaderChannel, priority, injectedPorts)
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

    data class DataSourceLink(
        val dataSource: DataSource,
        val varName: String,
        val deps: Map<String, DataSourceLink>
    ) : Link, ProgramNode {
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
//            dataSource.incomingLinks.forEach { (toPortId, fromLink) ->
//                val inputPort = shaderInstance.shader.findInputPort(toPortId)
//
//                val upstreamComponent = findUpstreamComponent(fromLink)
//                var expression = upstreamComponent.getExpression(prefix)
//                val type = upstreamComponent.resultType
//                if (inputPort.type != type) {
//                    expression = inputPort.contentType.adapt(expression, type)
//                }
//                tmpPortMap[toPortId] = expression
//            }
            return DataSourceComponent(dataSource, varName,
                deps.mapValues { (_, dataSourceLink) ->
                    findUpstreamComponent(dataSourceLink)
                }
            )
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

    class InjectedDataLink : Link {
        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode {
            return object : ExprNode() {
                override val title: String get() = "InjectedDataLink(${inputPort.id})"
                override val outputPort: OutputPort get() = OutputPort(inputPort.contentType)
                override val resultType: GlslType get() = inputPort.contentType.glslType

                override fun getExpression(prefix: String): GlslExpr {
                    return GlslExpr("${prefix}_global_${inputPort.id}")
                }
            }
        }
    }
}

fun DataSource.link(varName: String) = LiveShaderInstance.DataSourceLink(this, varName, emptyMap())

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

        return build(shader, shaderInstance, links)
            .also { liveShaderInstances[id] = it }
    }

    fun getResolvedShaderInstances() = liveShaderInstances

    companion object {
        fun build(
            shader: OpenShader,
            shaderInstance: ShaderInstance,
            links: Map<String, LiveShaderInstance.Link>
        ): LiveShaderInstance {
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
            );
        }

        private val logger = Logger("ShaderInstanceResolver")
    }
}
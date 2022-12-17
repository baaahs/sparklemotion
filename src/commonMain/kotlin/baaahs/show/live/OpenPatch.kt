package baaahs.show.live

import baaahs.app.ui.patchmod.PatchMod
import baaahs.fixtures.Fixture
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.*
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.show.Feed
import baaahs.show.Stream
import baaahs.show.Surfaces
import baaahs.show.UnknownFeed
import baaahs.sm.webapi.Problem
import baaahs.sm.webapi.Severity

class OpenPatch(
    val shader: OpenShader,
    val incomingLinks: Map<String, Link>,
    val stream: Stream,
    val priority: Float = 0f,
    val extraLinks: Set<String> = emptySet(),
    val missingLinks: Set<String> = emptySet(),
    val injectedPorts: Set<String> = emptySet(),
    val surfaces: Surfaces = Surfaces.AllSurfaces,
    val patchMods: List<PatchMod> = emptyList()
) {
    val title get() = shader.title
    val serial = nextSerial++

    val isFilter: Boolean
        get() = with(shader) {
            inputPorts.any {
                it.contentType == outputPort.contentType && incomingLinks[it.id]?.let { link ->
                    link is StreamLink && link.stream == stream
                } == true
            }
        }

    val dataSources get() = incomingLinks.values.mapNotNull { link ->
        (link as? DataSourceLink)?.feed
    }

    val problems: List<Problem>
        get() =
            arrayListOf<Problem>().apply {
                dataSources.forEach { dataSource ->
                    val unknownDataSource = dataSource as? UnknownFeed
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

    fun maybeWithInjectedData(injectedData: Set<ContentType>): OpenPatch {
        val injectedPorts = mutableSetOf<String>()

        val newLinks = incomingLinks.mapValues { (portId, link) ->
            val inputPort = shader.findInputPort(portId)
            if (injectedData.contains(inputPort.contentType)) {
                injectedPorts.add(inputPort.id)
                InjectedDataLink()
            } else link
        }

        return if (injectedPorts.isNotEmpty()) {
            OpenPatch(shader, newLinks, stream, priority, extraLinks, missingLinks, injectedPorts)
        } else this
    }

    fun track() =
        PortDiagram.Track(stream, shader.outputPort.contentType)

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

        return LinkedPatch(shader, resolvedIncomingLinks, stream, priority, injectedPorts, patchMods)
    }

    override fun toString(): String {
        return "OpenPatch(shader=${shader.title}, incomingLinks=${incomingLinks.keys.sorted()}, stream=$stream)"
    }

    fun matches(fixture: Fixture) = surfaces.matches(fixture)

    interface Link {
        fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode
    }

    data class ShaderOutLink(val patch: OpenPatch) : Link {
        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode {
            return patch.finalResolve(resolver)
        }
    }

    data class DataSourceLink(
        val feed: Feed,
        val varName: String,
        val deps: Map<String, DataSourceLink>
    ) : Link, ProgramNode {
        override val title: String get() = feed.title
        override val outputPort: OutputPort get() = OutputPort(feed.contentType)

        override fun getNodeId(programLinker: ProgramLinker): String = varName

        override fun traverse(programLinker: ProgramLinker) {
            programLinker.visit(this)
        }

        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode =
            resolver.resolveChannel(
                inputPort.copy(contentType = feed.contentType),
                Stream(varName)
            )

        override fun buildComponent(
            id: String,
            index: Int,
            prefix: String,
            findUpstreamComponent: (ProgramNode) -> Component
        ): Component {
//            dataSource.incomingLinks.forEach { (toPortId, fromLink) ->
//                val inputPort = shader.findInputPort(toPortId)
//
//                val upstreamComponent = findUpstreamComponent(fromLink)
//                var expression = upstreamComponent.getExpression(prefix)
//                val type = upstreamComponent.resultType
//                if (inputPort.type != type) {
//                    expression = inputPort.contentType.adapt(expression, type)
//                }
//                tmpPortMap[toPortId] = expression
//            }
            return FeedComponent(feed, varName,
                deps.mapValues { (_, dataSourceLink) ->
                    findUpstreamComponent(dataSourceLink)
                }
            )
        }
    }

    data class StreamLink(val stream: Stream) : Link {
        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode =
            resolver.resolveChannel(inputPort, stream)
    }

    data class ConstLink(val glsl: String, val type: GlslType) : Link {
        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode {
            return ConstNode(glsl, OutputPort(ContentType.unknown(type), dataType = type))
        }
    }

    class InjectedDataLink : Link {
        override fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): ProgramNode {
            return resolver.resolve(PortDiagram.Track(Stream("main"), inputPort.contentType), inputPort.injectedData.values.toSet())
                ?: InjectedExprNode(inputPort)
        }

        data class InjectedExprNode(private val inputPort: InputPort) : ExprNode() {
            override val title: String get() = "InjectedDataLink(${inputPort.id})"
            override val outputPort: OutputPort get() = OutputPort(inputPort.contentType)
            override val resultType: GlslType get() = inputPort.contentType.glslType

            override fun getExpression(prefix: String): GlslExpr {
                return GlslExpr("${prefix}_global_${inputPort.id}")
            }
        }
    }

    companion object {
        private var nextSerial = 0
    }
}
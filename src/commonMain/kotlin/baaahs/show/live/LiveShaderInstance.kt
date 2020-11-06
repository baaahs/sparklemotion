package baaahs.show.live

import baaahs.getBang
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.patch.PortDiagram
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.show.*
import baaahs.util.CacheBuilder
import baaahs.util.Logger

class LiveShaderInstance(
    val shader: OpenShader,
    val incomingLinks: Map<String, Link>,
    val shaderChannel: ShaderChannel?,
    val priority: Float
) {
    private var inTraversal = false

    fun <T> traverse(fn: () -> T): T {
        if (inTraversal) throw LinkedPatch.CircularGraphException(this)
        inTraversal = true
        try {
            return fn()
        } finally {
            inTraversal = false
        }
    }

    fun finalResolve(resolver: PortDiagram.Resolver, shaderChannel: ShaderChannel) =
        LiveShaderInstance(
            shader,
            incomingLinks.mapValues { (portId, link) ->
                link.finalResolve(shader.findInputPort(portId), resolver)
            },
            shaderChannel,
            priority
        )

    fun release() {
        shader.release()
    }

    override fun toString(): String {
        return "LiveShaderInstance(shader=${shader.title}, incomingLinks=${incomingLinks.keys.sorted()}, shaderChannel=$shaderChannel)"
    }


    interface Link {
        fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): Link = this
    }

    data class ShaderOutLink(val shaderInstance: LiveShaderInstance) : Link
    data class DataSourceLink(val dataSource: DataSource, val varName: String) : Link
    data class ShaderChannelLink(val shaderChannel: ShaderChannel) : Link {
        override fun finalResolve(
            inputPort: InputPort,
            resolver: PortDiagram.Resolver
        ): Link {
            val contentType = inputPort.contentType
                ?: return NoOpLink.also {
                    // TODO: This should probably show a user-visible error.
                    logger.error {
                        "No content type specified for port ${inputPort.id};" +
                                " it's required to resolve on channel ${shaderChannel.id}" }
                }
            val resolved = resolver.resolve(shaderChannel, contentType)
            return if (resolved != null)
                ShaderOutLink(resolved)
            else {
                // TODO: This should probably show a user-visible error.
                logger.error {
                    "No upstream shader found for port ${inputPort.id}" +
                            " (${inputPort.contentType}) on channel ${shaderChannel.id}" }
                NoOpLink
            }
        }
    }
    data class ConstLink(val glsl: String) : Link
    object NoOpLink : Link

    companion object {
        private val logger = Logger<LiveShaderInstance>()
    }
}

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
                    is DataSourceRef -> LiveShaderInstance.DataSourceLink(findDataSource(portRef.dataSourceId), portRef.dataSourceId)
                    is ShaderChannelRef -> LiveShaderInstance.ShaderChannelLink(portRef.shaderChannel)
                    is OutputPortRef -> TODO()
                    is ConstPortRef -> LiveShaderInstance.ConstLink(portRef.glsl)
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
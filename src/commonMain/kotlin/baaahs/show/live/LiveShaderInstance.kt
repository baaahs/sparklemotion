package baaahs.show.live

import baaahs.Logger
import baaahs.getBang
import baaahs.gl.patch.PortDiagram
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.show.*
import baaahs.util.CacheBuilder

class LiveShaderInstance(
    val shader: OpenShader,
    val incomingLinks: Map<String, Link>,
    val shaderChannel: ShaderChannel?,
    val priority: Float
) {
    fun findDataSourceRefs(): List<DataSourceRef> {
        return incomingLinks.values.filterIsInstance<DataSourceRef>()
    }

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
                ?: return NoOpLink
            val resolved = resolver.resolve(shaderChannel, contentType)
            return if (resolved != null)
                ShaderOutLink(resolved)
            else
                NoOpLink
        }
    }
    data class ConstLink(val glsl: String) : Link
    object NoOpLink : Link
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
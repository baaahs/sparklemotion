package baaahs.show.live

import baaahs.ShowPlayer
import baaahs.getBang
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.InputPort
import baaahs.glshaders.OpenShader
import baaahs.show.*

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
        fun finalResolve(inputPort: InputPort, resolver: AutoWirer.PortDiagram.Resolver): Link = this
    }

    data class ShaderOutLink(val shaderInstance: LiveShaderInstance) : Link
    data class DataSourceLink(val dataSource: DataSource, val varName: String) : Link
    data class ShaderChannelLink(val shaderChannel: ShaderChannel) : Link {
        override fun finalResolve(
            inputPort: InputPort,
            resolver: AutoWirer.PortDiagram.Resolver
        ): Link {
            val contentType = inputPort.contentType ?: return NoOpLink
            val resolved = resolver.resolve(shaderChannel, contentType)
            return if (resolved != null) ShaderOutLink(resolved) else NoOpLink }
    }
    object NoOpLink : Link
}

class ShowOpener(private val show: Show, private val showPlayer: ShowPlayer) {
    private val openShaders = show.shaders.mapValues { (_, shader) ->
        showPlayer.openShader(shader, addToCache = true)
    }
    private val resolver = ShaderInstanceResolver(
        openShaders,
        show.shaderInstances,
        show.dataSources
    )

    fun openShow(): OpenShow {
        return OpenShow(show, showPlayer, resolver.getResolvedShaderInstances())
    }
}

class ShaderInstanceResolver(
    val openShaders: Map<String, OpenShader>,
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
        val links = shaderInstance.incomingLinks.mapValues { (_, portRef) ->
            when (portRef) {
                is ShaderOutPortRef -> LiveShaderInstance.ShaderOutLink(resolve(portRef.shaderInstanceId))
                is DataSourceRef -> LiveShaderInstance.DataSourceLink(findDataSource(portRef.dataSourceId), portRef.dataSourceId)
                is ShaderChannelRef -> LiveShaderInstance.ShaderChannelLink(portRef.shaderChannel)
                is OutputPortRef -> TODO()
            }
        }
        return LiveShaderInstance(shader, links, shaderInstance.shaderChannel, shaderInstance.priority).also {
            liveShaderInstances[id] = it
        }
    }

    fun getResolvedShaderInstances() = liveShaderInstances
}
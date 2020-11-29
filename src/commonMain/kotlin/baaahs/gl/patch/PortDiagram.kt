package baaahs.gl.patch

import baaahs.gl.shader.InputPort
import baaahs.show.DataSource
import baaahs.show.ShaderChannel
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.OpenPatch
import baaahs.util.Logger

class PortDiagram(
    dataSources: Map<String, DataSource>,
    val patches: List<OpenPatch>
) {
    private val dataSourceChannelLinks = dataSources.map { (id, dataSource) ->
        (id to dataSource.contentType) to LiveShaderInstance.DataSourceLink(dataSource, id)
    }.associate { it }
    private val candidates: Map<Lookup, List<ChannelEntry>>
    private val resolved = hashMapOf<Lookup, LiveShaderInstance>()

    init {
        val candidates = hashMapOf<Lookup, MutableList<ChannelEntry>>()
        var level = 0

        fun addToChannel(shaderChannel: ShaderChannel, contentType: ContentType, shaderInstance: LiveShaderInstance, level: Int) {
            candidates.getOrPut(shaderChannel to contentType) { arrayListOf() }
                .add(
                    ChannelEntry(
                        shaderInstance,
                        shaderInstance.priority,
                        level
                    )
                )
        }

        fun add(patch: OpenPatch) {
            patch.shaderInstances.forEach { liveShaderInstance ->
                if (liveShaderInstance.shaderChannel != null) {
                    val outputPort = liveShaderInstance.shader.outputPort
                    addToChannel(liveShaderInstance.shaderChannel, outputPort.contentType, liveShaderInstance, level)
                }
            }

            level++
        }

        patches.forEach { add(it) }

        this.candidates = candidates
    }

    fun resolvePatch(shaderChannel: ShaderChannel, contentType: ContentType): LinkedPatch? {
        return Resolver().resolve(shaderChannel, contentType)
            ?.let { ProgramLinker(it).buildLinkedPatch() }
    }

    inner class Resolver {
        private val channelIterators =
            hashMapOf<Lookup, Iterator<LiveShaderInstance>>()

        fun resolve(shaderChannel: ShaderChannel, contentType: ContentType): LiveShaderInstance? {
            return resolveNext(shaderChannel, contentType).also {
                logger.debug { "Resolved $shaderChannel/$contentType to $it"}
                if (it != null) resolved[shaderChannel to contentType] = it
            }
        }

        private fun nextOf(shaderChannel: ShaderChannel, contentType: ContentType): LiveShaderInstance? {
            resolved[shaderChannel to contentType]?.let { return it }

            val iterator = channelIterators.getOrPut(shaderChannel to contentType) {
                candidates[shaderChannel to contentType]
                    ?.sortedWith(
                        compareByDescending<ChannelEntry> { it.typePriority }
                            .thenByDescending { it.priority }
                            .thenByDescending { it.level }
                            .thenByDescending { it.shaderInstance.shader.title }
                    )
                    ?.map { it.shaderInstance }?.iterator()
                    ?: emptyList<LiveShaderInstance>().iterator()
            }
            return if (iterator.hasNext()) iterator.next() else null
        }

        private fun resolveNext(shaderChannel: ShaderChannel, contentType: ContentType): LiveShaderInstance? {
            val nextInstance = nextOf(shaderChannel, contentType)
            return nextInstance?.let { shaderInstance ->
                LiveShaderInstance(
                    shaderInstance.shader,
                    shaderInstance.incomingLinks.mapValues { (portId, link) ->
                        link.finalResolve(shaderInstance.shader.findInputPort(portId), this)
                    },
                    shaderChannel,
                    shaderInstance.priority
                )
            }
        }

        fun tryDataSource(shaderChannel: ShaderChannel, contentType: ContentType): LiveShaderInstance.DataSourceLink? {
            return dataSourceChannelLinks[shaderChannel.id to contentType]
        }

        fun resolveChannel(inputPort: InputPort, shaderChannel: ShaderChannel): LiveShaderInstance.Link? {
            val contentType = inputPort.contentType
                ?: return null.also {
                    // TODO: This should probably show a user-visible error.
                    logger.error {
                        "No content type specified for port ${inputPort.id};" +
                                " it's required to resolve on channel ${shaderChannel.id}" }
                }
            val resolved = resolve(shaderChannel, contentType)
            return if (resolved != null)
                LiveShaderInstance.ShaderOutLink(resolved)
            else {
                tryDataSource(shaderChannel, contentType)
                    ?: run {
                        // TODO: This should probably show a user-visible error.
                        logger.error {
                            "No upstream shader found for port ${inputPort.id}" +
                                    " (${inputPort.contentType}) on channel ${shaderChannel.id}" }
                        null
                    }
            }
        }
    }

    class ChannelEntry(val shaderInstance: LiveShaderInstance, val priority: Float, val level: Int) {
        val typePriority: Int get() = shaderInstance.shader.defaultPriority

        override fun toString(): String {
            return "ChannelEntry(shaderInstance=${shaderInstance.shader.title}, priority=$priority, level=$level)"
        }
    }

    companion object {
        private val logger = Logger("PortDiagram")
    }
}

private typealias Lookup = Pair<ShaderChannel, ContentType>
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
    private val candidates: Map<Track, Candidates>
    private val resolvedNodes = hashMapOf<LiveShaderInstance, ProgramNode>()

    init {
        val candidates = hashMapOf<Track, MutableList<ChannelEntry>>()
        var level = 0

        fun addToChannel(track: Track, shaderInstance: LiveShaderInstance, level: Int) {
            val channelTypeCandidates = candidates.getOrPut(track) { arrayListOf() }
            if (channelTypeCandidates.any { it.shaderInstance == shaderInstance }) {
                throw error("candidates for $track already include ${shaderInstance.shader.title}")
            }
            channelTypeCandidates.add(ChannelEntry(shaderInstance, shaderInstance.priority, level))
        }

        fun add(patch: OpenPatch) {
            patch.shaderInstances.forEach { liveShaderInstance ->
                if (liveShaderInstance.shaderChannel != null) {
                    val outputPort = liveShaderInstance.shader.outputPort
                    val track = Track(liveShaderInstance.shaderChannel, outputPort.contentType)
                    addToChannel(track, liveShaderInstance, level)
                }
            }

            level++
        }

        patches.forEach { add(it) }

        this.candidates = candidates.mapValues { (_, entries) -> Candidates(entries) }
    }

    fun resolvePatch(shaderChannel: ShaderChannel, contentType: ContentType): LinkedPatch? {
        val track = Track(shaderChannel, contentType)
        val resolver = Resolver()
        val rootProgramNode = resolver.resolve(track)

        return if (rootProgramNode != null) {
            logger.debug { "Resolved $track to $rootProgramNode." }
            ProgramLinker(rootProgramNode, resolver.warnings).buildLinkedPatch()
        } else {
            logger.warn { "Failed to resolve $track." }
            null
        }
    }

    data class Track(val shaderChannel: ShaderChannel, val contentType: ContentType) {
        override fun toString(): String {
            return "Track[${shaderChannel.id}/${contentType.id}]"
        }
    }

    private class Candidates(entries: List<ChannelEntry>) {
        val sortedEntries = entries.sortedWith(
            compareByDescending<ChannelEntry> { it.typePriority }
                .thenByDescending { it.priority }
                .thenByDescending { it.level }
                .thenByDescending { it.shaderInstance.shader.title }
        )

        fun iterator(): Iterator<LiveShaderInstance> {
            return sortedEntries
                .map { it.shaderInstance }
                .iterator()
        }
    }

    class Breadcrumb(
        val track: Track
    ) {
        var instance: LiveShaderInstance? = null
        var resolvingInputPort: InputPort? = null
        override fun toString(): String {
            return "Resolving $track" +
                    (instance?.let { " -> [${it.shader.title}]"} ?: "") +
                    (resolvingInputPort?.let { ".${it.id} (${it.contentType?.id ?: "unknown content type"})"} ?: "")
        }
    }

    inner class Resolver {
        private val trackResolvers = mutableMapOf<Track, TrackResolver>()
        private val breadcrumbs = mutableListOf<Breadcrumb>()
        private val currentBreadcrumb get() = breadcrumbs.last()
        internal val warnings = mutableListOf<String>()

        fun resolve(track: Track): ProgramNode? {
            return trackResolvers.getOrPut(track) { TrackResolver(track) }
                .resolve()
        }

        fun resolveChannel(inputPort: InputPort, shaderChannel: ShaderChannel): ProgramNode {
            val contentType = inputPort.contentType
                ?: throw ResolveException(
                    "attempt to resolve a channel for a port with no content type",
                    message = "resolveChannel(inputPort=${inputPort.id}, channel=${shaderChannel.id}))"
                )

            val track = Track(shaderChannel, contentType)
            return resolve(track)
                ?: tryDataSource(shaderChannel, contentType)
                ?: run {
                    addWarning("No upstream shader found, using default for ${contentType.id}.")
                    DefaultValueNode(contentType)
                }
        }

        private fun addWarning(message: String) {
            warnings.add("$message\n" +
                    "Stack:" +
                    breadcrumbs.asReversed().joinToString { "\n    $it" }
            )
        }

        fun resolveLink(inputPort: InputPort, link: LiveShaderInstance.Link): ProgramNode {
            currentBreadcrumb.resolvingInputPort = inputPort
            return link.finalResolve(inputPort, this@Resolver)
        }

        private fun tryDataSource(
            shaderChannel: ShaderChannel,
            contentType: ContentType
        ): LiveShaderInstance.DataSourceLink? {
            return dataSourceChannelLinks[shaderChannel.id to contentType]
        }


        inner class TrackResolver(private val track: Track) {
            private val channelIterators = hashMapOf<Track, Iterator<LiveShaderInstance>>()
            private val dagAncestors = hashSetOf<LiveShaderInstance>()

            private var resolved = false
            private var resolution: ProgramNode? = null

            fun resolve(): ProgramNode? {
                if (resolved) return resolution

                breadcrumbs.add(Breadcrumb(track))

                try {
                    val nextInstance = try {
                        val iterator = channelIterators.getOrPut(track) {
                            candidates[track]?.iterator()
                                ?: emptyList<LiveShaderInstance>().iterator()
                        }
                        if (iterator.hasNext())
                            iterator.next()
                        else null
                    } catch (e: ResolveException) {
                        throw e.chain("Resolver.resolve($track)")
                    }

                    currentBreadcrumb.instance = nextInstance

                    return nextInstance?.let { shaderInstance ->
                        if (!dagAncestors.add(shaderInstance)) {
                            throw ResolveException(
                                "circular reference",
                                message = "resolve($track) already saw [${shaderInstance.shader.title}]"
                            )
                        }

                        try {
                            resolvedNodes.getOrPut(shaderInstance) {
                                shaderInstance.finalResolve(this@Resolver)
                            }
                        } finally {
                            dagAncestors.remove(shaderInstance)
                        }
                    }.also {
                        resolved = true
                        resolution = it
                    }
                } finally {
                    breadcrumbs.removeLast()

                    if (breadcrumbs.isEmpty() && warnings.isNotEmpty()) {
                        logger.error { warnings.joinToString("\n\n") }
                    }
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

    class ResolveException(
        private val initialComplaint: String,
        private val stack: List<String>,
        cause: Exception? = null
    ) : Exception("$initialComplaint: ${stack.joinToString(" -> ")}", cause) {
        constructor(
            initialComplaint: String,
            message: String
        ) : this(initialComplaint, listOf(message))

        fun chain(message: String): Throwable {
            return ResolveException(initialComplaint, listOf(message) + stack, this)
        }
    }

    companion object {
        private val logger = Logger("PortDiagram")
    }
}
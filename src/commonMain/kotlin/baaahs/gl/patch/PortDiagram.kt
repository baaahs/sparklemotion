package baaahs.gl.patch

import baaahs.getBang
import baaahs.gl.shader.InputPort
import baaahs.show.DataSource
import baaahs.show.ShaderChannel
import baaahs.show.live.OpenPatch
import baaahs.util.Logger

class PortDiagram(val patches: List<OpenPatch>) {
    private val candidates: Map<Track, Candidates>
    private val resolvedNodes = hashMapOf<OpenPatch, ProgramNode>()

    init {
        val candidates = hashMapOf<Track, MutableList<ChannelEntry>>()
        var level = 0

        fun addToChannel(track: Track, openPatch: OpenPatch, level: Int) {
            val channelTypeCandidates = candidates.getOrPut(track) { arrayListOf() }
            if (channelTypeCandidates.any { it.openPatch == openPatch }) {
                error("candidates for $track already include ${openPatch.shader.title}")
            }
            channelTypeCandidates.add(ChannelEntry(openPatch, openPatch.priority, level))
        }

        fun add(patch: OpenPatch) {
            val outputPort = patch.shader.outputPort
            val track = Track(patch.shaderChannel, outputPort.contentType)
            addToChannel(track, patch, level)

            level++
        }

        patches.forEach { add(it) }

        this.candidates = candidates.mapValues { (_, entries) -> Candidates(entries) }
    }

    fun resolvePatch(
        shaderChannel: ShaderChannel,
        contentType: ContentType,
        dataSources: Map<String, DataSource>
    ): LinkedProgram? {
        val resolver = Resolver(dataSources)
        val track = Track(shaderChannel, contentType)
        val rootProgramNode = resolver.resolve(track)

        return if (rootProgramNode != null) {
            logger.debug { "Resolved $track to $rootProgramNode." }
            ProgramLinker(rootProgramNode, resolver.warnings).buildLinkedProgram()
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

    internal class Candidates(entries: List<ChannelEntry>) {
        companion object {
            val comparator =
                compareByDescending<ChannelEntry> { it.priority }
                    .thenByDescending { it.typePriority }
                    .thenByDescending { it.level }
                    .thenBy { it.openPatch.shader.title }
        }

        private val sortedEntries = entries.sortedWith(comparator)

        fun iterator(): Iterator<OpenPatch> {
            return sortedEntries
                .map { it.openPatch }
                .iterator()
        }
    }

    class Breadcrumb(
        val track: Track
    ) {
        var openPatch: OpenPatch? = null
        var resolvingInputPort: InputPort? = null
        override fun toString(): String {
            return "Resolving $track" +
                    (openPatch?.let { " -> [${it.shader.title}]" } ?: "") +
                    (resolvingInputPort?.let { ".${it.id} (${it.contentType.id})" } ?: "")
        }
    }

    inner class Resolver(
        dataSources: Map<String, DataSource>
    ) {
        private val dataSourcesToId by lazy { dataSources.entries.associate { (k, v) -> v to k } }
        private val dataSourceLinks = mutableMapOf<Pair<String, ContentType>, OpenPatch.DataSourceLink>()

        private fun resolveDataSource(id: String, dataSource: DataSource): OpenPatch.DataSourceLink {
            return dataSourceLinks.getOrPut(id to dataSource.contentType) {
                val deps = dataSource.dependencies.mapValues { (_, dataSource) ->
                    val dependencyId = dataSourcesToId.getBang(dataSource, "data source dependency")
                    resolveDataSource(dependencyId, dataSource)
                }
                OpenPatch.DataSourceLink(dataSource, id, deps)
            }
        }

        init {
            dataSources.forEach { (id, dataSource) ->
                resolveDataSource(id, dataSource)
            }
        }

        private val dataSourceChannelLinks = dataSourceLinks.toMap()

        private val trackResolvers = mutableMapOf<Track, TrackResolver>()
        private val breadcrumbs = mutableListOf<Breadcrumb>()
        private val currentBreadcrumb get() = breadcrumbs.last()
        internal val warnings = mutableListOf<String>()

        fun resolve(track: Track, injectedData: Set<ContentType> = emptySet()): ProgramNode? {
            return trackResolvers.getOrPut(track) { TrackResolver(track) }
                .resolve(injectedData)
        }

        fun resolveChannel(inputPort: InputPort, shaderChannel: ShaderChannel): ProgramNode {
            val contentType = inputPort.contentType

            val track = Track(shaderChannel, contentType)
            return resolve(track, inputPort.injectedData.values.toSet())
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

        fun resolveLink(inputPort: InputPort, link: OpenPatch.Link): ProgramNode {
            currentBreadcrumb.resolvingInputPort = inputPort
            return link.finalResolve(inputPort, this@Resolver)
        }

        private fun tryDataSource(
            shaderChannel: ShaderChannel,
            contentType: ContentType
        ): OpenPatch.DataSourceLink? {
            return dataSourceChannelLinks[shaderChannel.id to contentType]
        }


        inner class TrackResolver(private val track: Track) {
            private val channelIterators = hashMapOf<Track, Iterator<OpenPatch>>()
            private val dagAncestors = hashSetOf<OpenPatch>()

            private var resolved = false
            private var resolution: ProgramNode? = null

            fun resolve(injectedData: Set<ContentType>): ProgramNode? {
                if (resolved) return resolution

                breadcrumbs.add(Breadcrumb(track))

                try {
                    val nextInstance = try {
                        val iterator = channelIterators.getOrPut(track) {
                            candidates[track]?.iterator()
                                ?: emptyList<OpenPatch>().iterator()
                        }
                        if (iterator.hasNext())
                            iterator.next()
                        else null
                    } catch (e: ResolveException) {
                        throw e.chain("Resolver.resolve($track)")
                    }

                    currentBreadcrumb.openPatch = nextInstance

                    return nextInstance
                        ?.maybeWithInjectedData(injectedData)
                        ?.let { openPatch ->
                            if (!dagAncestors.add(openPatch)) {
                                throw ResolveException(
                                    "circular reference",
                                    message = "resolve($track) already saw [${openPatch.shader.title}]"
                                )
                            }

                            try {
                                resolvedNodes.getOrPut(openPatch) {
                                    openPatch.finalResolve(this@Resolver)
                                }
                            } finally {
                                dagAncestors.remove(openPatch)
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

    class ChannelEntry(val openPatch: OpenPatch, val priority: Float, val level: Int) {
        val typePriority: Int get() = if (openPatch.isFilter) 1 else 0

        override fun toString(): String {
            return "ChannelEntry(shader=${openPatch.shader.title}, priority=$priority, level=$level)"
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
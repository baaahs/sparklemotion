package baaahs.gl.patch

import baaahs.device.FixtureType
import baaahs.getBang
import baaahs.gl.shader.InputPort
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.Stream
import baaahs.show.live.OpenPatch
import baaahs.util.Logger

class PortDiagram(
    val patches: List<OpenPatch>,
    private val fixtureType: FixtureType
) {
    internal val candidates: Map<Track, Candidates>
    private val resolvedNodes = hashMapOf<OpenPatch, ProgramNode>()

    init {
        val candidates = hashMapOf<Track, MutableList<PatchCandidate>>()
        var level = 0

        fun candidatesFor(track: Track, block: MutableList<PatchCandidate>.() -> Unit) {
            val trackCandidates = candidates.getOrPut(track) { arrayListOf() }
            block(trackCandidates)
        }

        fun addToChannel(openPatch: OpenPatch, level: Int) {
            val track = openPatch.track()
            candidatesFor(track) {
                if (any { it.openPatch == openPatch }) {
                    error("candidates for $track already include ${openPatch.shader.title}")
                }
                add(PatchCandidate(openPatch, level = level))
            }
        }

        fun add(patch: OpenPatch) {
            addToChannel(patch, level)

            level++
        }

        patches.forEach { add(it) }

        this.candidates = candidates.mapValues { (_, entries) -> Candidates(entries) }
    }

    fun resolvePatch(
        stream: Stream,
        contentType: ContentType,
        feeds: Map<String, Feed>
    ): LinkedProgram? {
        val resolver = Resolver(feeds)
        val track = Track(stream, contentType)
        val rootProgramNode = resolver.resolve(track)

        return if (rootProgramNode != null) {
            logger.debug { "Resolved $track to $rootProgramNode." }
            ProgramLinker(rootProgramNode, resolver.warnings, fixtureType).buildLinkedProgram()
        } else {
            logger.warn { "Failed to resolve $track." }
            null
        }
    }

    data class Track(val stream: Stream, val contentType: ContentType) {
        override fun toString(): String {
            return "Track[${stream.id}/${contentType.id}]"
        }
    }

    internal class Candidates(entries: List<PatchCandidate>) {
        companion object {
            val comparator =
                compareByDescending<PatchCandidate> { it.priority }
                    .thenByDescending { it.typePriority }
                    .thenByDescending { it.level }
                    .thenBy { it.title }
        }

        internal val sortedEntries = entries.sortedWith(comparator)

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
        feeds: Map<String, Feed>
    ) {
        private val feedsToId by lazy { feeds.entries.associate { (k, v) -> v to k } }
        private val feedLinks = mutableMapOf<Pair<String, ContentType>, OpenPatch.FeedLink>()

        private fun resolveFeed(id: String, feed: Feed): OpenPatch.FeedLink {
            return feedLinks.getOrPut(id to feed.contentType) {
                val deps = resolveFeedDeps(feed)
                OpenPatch.FeedLink(feed, id, deps)
            }
        }

        private fun resolveFeedDeps(feed: Feed): Map<String, OpenPatch.FeedLink> =
            feed.dependencies.mapValues { (_, depFeed) ->
                val dependencyId = feedsToId.getBang(depFeed, "feed dependency")
                resolveFeed(dependencyId, depFeed)
            }

        init {
            feeds.forEach { (id, feed) ->
                resolveFeed(id, feed)
            }
        }

        private val feedChannelLinks = feedLinks.toMap()

        private val trackResolvers = mutableMapOf<Track, TrackResolver>()
        private val breadcrumbs = mutableListOf<Breadcrumb>()
        private val currentBreadcrumb get() = breadcrumbs.last()
        internal val warnings = mutableListOf<String>()

        fun resolve(track: Track, injectedData: Set<ContentType> = emptySet()): ProgramNode? {
            return trackResolvers.getOrPut(track) { TrackResolver(track) }
                .resolve(injectedData)
        }

        fun resolveChannel(inputPort: InputPort, stream: Stream): ProgramNode {
            val contentType = inputPort.contentType

            val track = Track(stream, contentType)
            return resolve(track, inputPort.injectedData.values.toSet())
                ?: tryFeed(stream, contentType)
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

        private fun tryFeed(
            stream: Stream,
            contentType: ContentType
        ): OpenPatch.FeedLink? {
            return feedChannelLinks[stream.id to contentType]
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
                        logger.warn { warnings.joinToString("\n\n") }
                    }
                }
            }
        }
    }

    class PatchCandidate(
        val openPatch: OpenPatch,
        val priority: Float = openPatch.priority,
        val level: Int = 0
    )  {
        val typePriority: Int
            get() = if (openPatch.isFilter) 1 else 0
        val title: String
            get() = openPatch.title

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
package baaahs.glshaders

import baaahs.Logger
import baaahs.show.*
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.OpenPatch
import baaahs.show.live.OpenPatchHolder
import baaahs.show.mutable.*
import baaahs.unknown

class AutoWirer(
    val plugins: Plugins,
    val glslAnalyzer: GlslAnalyzer = GlslAnalyzer()
) {
    fun autoWire(
        vararg shaders: Shader,
        focus: Shader? = null
    ): UnresolvedPatch {
        val openShaders = shaders.associate { it to glslAnalyzer.openShader(it) }
        return autoWire(openShaders.values, focus?.let { openShaders[it] })
    }

    fun autoWire(
        vararg shaders: OpenShader,
        focus: OpenShader? = null
    ): UnresolvedPatch {
        return autoWire(shaders.toList(), focus)
    }

    fun autoWire(
        shaders: Collection<OpenShader>,
        focus: OpenShader? = null,
        shaderChannel: ShaderChannel = ShaderChannel.Main
    ): UnresolvedPatch {
        val locallyAvailable: MutableMap<ContentType, MutableSet<MutableLink.Port>> = mutableMapOf()

        // First pass: gather shader output ports.
        val shaderInstances = shaders.associate { openShader ->
            val unresolvedShaderInstance = UnresolvedShaderInstance(
                MutableShader(openShader.shader),
                openShader.inputPorts.map { it.id }.associateWith { hashSetOf<MutableLink.Port>() },
                shaderChannel
            )

            locallyAvailable.getOrPut(openShader.outputPort.contentType) { mutableSetOf() }
                .add(UnresolvedShaderOutPort(unresolvedShaderInstance, openShader.outputPort.id))

            openShader.shaderType.defaultUpstreams.forEach { (contentType, shaderChannel) ->
                locallyAvailable.getOrPut(contentType) { mutableSetOf() }
                    .add(MutableShaderChannel(shaderChannel))
            }

            openShader to unresolvedShaderInstance
        }

        // Second pass: link datasources/output ports to input ports.
        val shaderInstancesOfInterest = if (focus == null) {
            shaderInstances
        } else {
            mapOf(focus to (shaderInstances[focus] ?: error("missing shader editor?")))
        }

        val dataSources = hashSetOf<DataSource>()
        val unresolvedShaderInstances = shaderInstancesOfInterest.map { (openShader, unresolvedShaderInstance) ->
            openShader.inputPorts.forEach { inputPort ->
                val localSuggestions: Set<MutableLink.Port>? = locallyAvailable[inputPort.contentType]
                val suggestions = localSuggestions ?: plugins.suggestDataSources(inputPort).map {
                    dataSources.add(it)
                    MutableDataSource(it)
                }
                unresolvedShaderInstance.incomingLinksOptions[inputPort.id]!!.addAll(suggestions)
            }
            unresolvedShaderInstance
        }
        return UnresolvedPatch(unresolvedShaderInstances, dataSources.toList())
    }

    data class UnresolvedShaderOutPort(
        val unresolvedShaderInstance: UnresolvedShaderInstance,
        val portId: String
    ) : MutableLink.Port {
        override fun toRef(showBuilder: ShowBuilder): PortRef = TODO("not implemented")
        override fun displayName(): String = TODO("not implemented")
    }

    data class UnresolvedShaderInstance(
        val mutableShader: MutableShader,
        val incomingLinksOptions: Map<String, MutableSet<MutableLink.Port>>,
        var shaderChannel: ShaderChannel? = null
    ) {
        fun isAmbiguous() = incomingLinksOptions.values.any { it.size > 1 }

        fun describeAmbiguity(): String {
            return mutableShader.title + ": " +
                    incomingLinksOptions
                        .filter { (_, links) -> links.size > 1 }
                        .map { (portId, links) ->
                            "$portId->(${links.joinToString(",") { it.displayName() }}"
                        }
        }

        fun acceptSymbolicChannelLinks() {
            incomingLinksOptions.values.forEach { options ->
                val shaderChannelOptions = options.filterIsInstance<MutableShaderChannel>()
                if (options.size > 1 && shaderChannelOptions.size == 1) {
                    options.clear()
                    options.add(shaderChannelOptions.first())
                }
            }
        }
    }

    fun merge(vararg patchHolders: OpenPatchHolder): Map<Surfaces, LinkedPatch> {
        val patchesBySurfaces = mutableMapOf<Surfaces, MutableList<OpenPatch>>()
        patchHolders.forEach { openPatchHolder ->
            openPatchHolder.patches.forEach { patch ->
                patchesBySurfaces.getOrPut(patch.surfaces) { arrayListOf() }
                    .add(patch)
            }
        }
        return merge(patchesBySurfaces)
    }

    fun merge(patchesBySurfaces: Map<Surfaces, List<OpenPatch>>): Map<Surfaces, LinkedPatch> {
        return patchesBySurfaces.mapValues { (surfaces, openPatches) ->
            val shaderInstance = merge(*openPatches.toTypedArray())
            shaderInstance?.let { LinkedPatch(it, surfaces) }
        }.mapNotNull { (k, v) -> v?.let { k to v } }.associate { it }
    }

    fun merge(vararg patches: OpenPatch): LiveShaderInstance? {
        val portDiagram = PortDiagram()
        patches.forEach { patch ->
            portDiagram.add(patch)
        }
        return portDiagram.resolvePatch(ShaderChannel.Main, ContentType.Color)
    }

    class ChannelEntry(val shaderInstance: LiveShaderInstance, val priority: Int, val level: Int) {
        val typePriority: Int get() = shaderInstance.shader.shaderType.priority

        override fun toString(): String {
            return "ChannelEntry(shaderInstance=${shaderInstance.shader.title}, priority=$priority, level=$level)"
        }
    }

    class PortDiagram {
        private var surfaces: Surfaces? = null
        private var level = 0
        private val mutablePatch = MutablePatch()
        private val candidates = hashMapOf<Pair<ShaderChannel, ContentType>, MutableList<ChannelEntry>>()
        private val resolved = hashMapOf<Pair<ShaderChannel, ContentType>, LiveShaderInstance>()

        private fun addToChannel(shaderChannel: ShaderChannel, contentType: ContentType, shaderInstance: LiveShaderInstance, level: Int) {
            candidates.getOrPut(shaderChannel to contentType) { arrayListOf() }
                .add(ChannelEntry(shaderInstance, 0, level))
        }

        fun add(patch: OpenPatch) {
            if (surfaces == null) {
                surfaces = patch.surfaces
                mutablePatch.surfaces = patch.surfaces
            } else if (surfaces != patch.surfaces) {
                error("Surface mismatch: $surfaces != ${patch.surfaces}")
            }

            patch.shaderInstances.forEach { liveShaderInstance ->
                if (liveShaderInstance.shaderChannel != null) {
                    val outputPort = liveShaderInstance.shader.outputPort
                    addToChannel(liveShaderInstance.shaderChannel, outputPort.contentType, liveShaderInstance, level)
                }
            }

            level++
        }

        fun resolvePatch(shaderChannel: ShaderChannel, contentType: ContentType): LiveShaderInstance? {
            return Resolver().resolve(shaderChannel, contentType)
        }

        inner class Resolver {
            private val channelIterators =
                hashMapOf<Pair<ShaderChannel, ContentType>, Iterator<LiveShaderInstance>>()

            fun resolve(shaderChannel: ShaderChannel, contentType: ContentType): LiveShaderInstance? {
                return resolveNext(shaderChannel, contentType).also {
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
                return nextOf(shaderChannel, contentType)?.let { shaderInstance ->
                    LiveShaderInstance(
                        shaderInstance.shader,
                        shaderInstance.incomingLinks.mapValues { (portId, link) ->
                            link.finalResolve(shaderInstance.shader.findInputPort(portId), this)
                        },
                        shaderChannel
                    )
                }
            }
        }
    }

    class PortRefCandidates() {
        private val fromPorts = arrayListOf<Pair<PortRef, Int>>()
        var iterator: Iterator<Pair<PortRef, Int>>? = null

        fun add(from: PortRef, priority: Int) {
            fromPorts.add(from to priority)
        }

        fun findBest(): PortRef? {
            if (iterator == null) iterator = fromPorts.iterator()
            return if (iterator!!.hasNext()) iterator!!.next().first else null
        }
    }

    data class UnresolvedPatch(
        private val unresolvedShaderInstances: List<UnresolvedShaderInstance>,
        private val dataSources: List<DataSource>
    ) {
        fun isAmbiguous() = unresolvedShaderInstances.any { it.isAmbiguous() }

        fun resolve(): MutablePatch {
            if (isAmbiguous()) {
                error("ambiguous! " +
                        unresolvedShaderInstances
                            .filter { it.isAmbiguous() }
                            .map { it.describeAmbiguity() }
                )
            }

            // First pass: create a shader instance editor for each shader.
            val shaderInstances = unresolvedShaderInstances.associate {
                it.mutableShader.build() to MutableShaderInstance(
                    it.mutableShader,
                    it.incomingLinksOptions.mapValues { (_, fromPortOptions) ->
                        fromPortOptions.first()
                    }.toMutableMap(),
                    it.shaderChannel
                )
            }

            // Second pass: resolve references between shaders to the correct instance editor.
            shaderInstances.values.forEach { shaderInstance ->
                shaderInstance.incomingLinks.forEach { (toPortId, fromPort) ->
                    if (fromPort is UnresolvedShaderOutPort) {
                        val fromShader = fromPort.unresolvedShaderInstance.mutableShader.build()
                        val fromShaderInstance = shaderInstances[fromShader]
                            ?: error(unknown("shader instance editor", fromShader, shaderInstances.keys))
                        shaderInstance.incomingLinks[toPortId] =
                            MutableShaderOutPort(
                                fromShaderInstance,
                                fromPort.portId
                            )
                    }
                }
            }

            return MutablePatch(
                shaderInstances.values.toList(),
                Surfaces.AllSurfaces
            )
        }

        fun acceptSymbolicChannelLinks(): UnresolvedPatch {
            unresolvedShaderInstances.forEach { it.acceptSymbolicChannelLinks() }
            return this
        }
    }

    companion object {
        private val logger = Logger("AutoWirer")
    }
}
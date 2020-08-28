package baaahs.gl.patch

import baaahs.Logger
import baaahs.show.*
import baaahs.show.live.OpenShaders
import baaahs.show.mutable.MutablePatch

class PortDiagram(private val openShaders: OpenShaders) {
    private var surfaces: Surfaces? = null
    private var level = 0
    private val mutablePatch = MutablePatch()
    private val candidates = hashMapOf<Pair<ShaderChannel, ContentType>, MutableList<ChannelEntry>>()
    private val resolved = hashMapOf<Pair<ShaderChannel, ContentType>, ShaderInstance>()

    private fun addToChannel(shaderChannel: ShaderChannel, contentType: ContentType, shaderInstance: ShaderInstance, level: Int) {
        candidates.getOrPut(shaderChannel to contentType) { arrayListOf() }
            .add(
                ChannelEntry(
                    shaderInstance,
                    openShaders.getOpenShader(shaderInstance.shader).shaderType,
                    shaderInstance.priority,
                    level
                )
            )
    }

    fun add(patch: Patch) {
        if (surfaces == null) {
            surfaces = patch.surfaces
            mutablePatch.surfaces = patch.surfaces
        } else if (surfaces != patch.surfaces) {
            error("Surface mismatch: $surfaces != ${patch.surfaces}")
        }

        patch.shaderInstances.forEach { shaderInstance ->
            val openShader = openShaders.getOpenShader(shaderInstance.shader)
            val outputPort = openShader.outputPort
            addToChannel(shaderInstance.shaderChannel, outputPort.contentType, shaderInstance, level)
        }

        level++
    }

    fun resolvePatch(shaderChannel: ShaderChannel, contentType: ContentType): LinkedPatch? {
        return Resolver().resolve(shaderChannel, contentType)
            ?.let { LinkedPatch(it, surfaces!!, openShaders) }
    }

    inner class Resolver {
        private val channelIterators =
            hashMapOf<Pair<ShaderChannel, ContentType>, Iterator<ShaderInstance>>()

        fun resolve(shaderChannel: ShaderChannel, contentType: ContentType): ShaderInstance? {
            return resolveNext(shaderChannel, contentType).also {
                logger.debug { "Resolved $shaderChannel/$contentType to $it"}
                if (it != null) resolved[shaderChannel to contentType] = it
            }
        }

        private fun nextOf(shaderChannel: ShaderChannel, contentType: ContentType): ShaderInstance? {
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
                    ?: emptyList<ShaderInstance>().iterator()
            }
            return if (iterator.hasNext()) iterator.next() else null
        }

        private fun resolveNext(shaderChannel: ShaderChannel, contentType: ContentType): ShaderInstance? {
            val nextInstance = nextOf(shaderChannel, contentType)
            return nextInstance?.let { shaderInstance ->
                ShaderInstance(
                    shaderInstance.shader,
                    shaderInstance.incomingLinks.mapValues { (portId, link) ->
                        val inputPort = openShaders.getOpenShader(shaderInstance.shader).findInputPort(portId)
                        link.finalResolve(inputPort, this)
                    },
                    shaderChannel,
                    shaderInstance.priority
                )
            }
        }
    }

    class ChannelEntry(
        val shaderInstance: ShaderInstance,
        val shaderType: ShaderType,
        val priority: Float,
        val level: Int
    ) {
        val typePriority: Int get() = shaderType.priority

        override fun toString(): String {
            return "ChannelEntry(shaderInstance=${shaderInstance.shader.title}, priority=$priority, level=$level)"
        }
    }

    companion object {
        private val logger = Logger("PortDiagram")
    }
}
package baaahs.gl.patch

import baaahs.show.ShaderChannel
import baaahs.show.Surfaces
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.OpenPatch
import baaahs.show.mutable.MutablePatch

class PortDiagram {
    private var surfaces: Surfaces? = null
    private var level = 0
    private val mutablePatch = MutablePatch()
    private val candidates = hashMapOf<Pair<ShaderChannel, ContentType>, MutableList<ChannelEntry>>()
    private val resolved = hashMapOf<Pair<ShaderChannel, ContentType>, LiveShaderInstance>()

    private fun addToChannel(shaderChannel: ShaderChannel, contentType: ContentType, shaderInstance: LiveShaderInstance, level: Int) {
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

    fun resolvePatch(shaderChannel: ShaderChannel, contentType: ContentType): LinkedPatch? {
        return Resolver().resolve(shaderChannel, contentType)
            ?.let { LinkedPatch(it, surfaces!!) }
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
    }

    class ChannelEntry(val shaderInstance: LiveShaderInstance, val priority: Float, val level: Int) {
        val typePriority: Int get() = shaderInstance.shader.shaderType.priority

        override fun toString(): String {
            return "ChannelEntry(shaderInstance=${shaderInstance.shader.title}, priority=$priority, level=$level)"
        }
    }
}
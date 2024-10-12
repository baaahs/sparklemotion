package baaahs.show.live

import baaahs.app.ui.patchmod.patchModBuilders
import baaahs.getBang
import baaahs.gl.Toolchain
import baaahs.gl.glsl.GlslType
import baaahs.gl.shader.OpenShader
import baaahs.show.*
import baaahs.util.CacheBuilder
import baaahs.util.Logger

class PatchResolver(
    private val openShaders: CacheBuilder<String, OpenShader>,
    private val patches: Map<String, Patch>,
    private val feeds: Map<String, Feed>,
    private val toolchain: Toolchain,
    private val gadgetProvider: GadgetProvider? = null
) {
    private val openPatches = hashMapOf<String, OpenPatch>()

    init {
        patches.keys.forEach { patchId ->
            resolve(patchId)
        }
    }

    private fun findFeed(id: String) = feeds.getBang(id, "feed")
    private fun findShader(id: String): OpenShader = openShaders.getBang(id, "open shader")
    private fun findPatch(id: String): Patch = patches.getBang(id, "patch")

    private fun resolve(id: String): OpenPatch {
        openPatches[id]?.let { return it }

        val patch = findPatch(id)
        val shader = findShader(patch.shaderId)
        val knownInputPorts = shader.inputPorts.associateBy { it.id }

        val links = patch.incomingLinks
            .filterKeys { portId ->
                knownInputPorts.contains(portId).also { containsKey ->
                    if (!containsKey)
                        logger.debug {
                            "Unknown port mapping \"$portId\" for shader \"${shader.title}\" " +
                                    "(have ${knownInputPorts.keys.sorted()})"
                        }
                }
            }
            .mapValues { (_, portRef) ->
                when (portRef) {
                    is FeedRef -> findFeed(portRef.feedId).link(portRef.feedId)
                    is StreamRef -> OpenPatch.StreamLink(portRef.stream)
                    is OutputPortRef -> TODO()
                    is ConstPortRef -> OpenPatch.ConstLink(portRef.glsl, GlslType.from(portRef.type))
                }
            }

        return build(shader, patch, links, toolchain, gadgetProvider, id)
            .also { openPatches[id] = it }
    }

    fun getResolvedPatches() = openPatches

    companion object {
        fun build(
            shader: OpenShader,
            patch: Patch,
            links: Map<String, OpenPatch.Link>,
            toolchain: Toolchain,
            gadgetProvider: GadgetProvider? = null,
            patchId: String? = null
        ): OpenPatch {
            val ports = shader.inputPorts.map { it.id }
            val extraLinks = patch.incomingLinks.keys - ports
            val missingLinks = ports - patch.incomingLinks.keys

            val patchMods = if (gadgetProvider != null && patchId != null) {
                patchModBuilders.mapNotNull { patchModBuilder ->
                    patchModBuilder.buildIfRelevant(shader, patchId, toolchain, gadgetProvider)
                }
            } else emptyList()

            return OpenPatch(
                shader,
                links,
                patch.stream,
                patch.priority,
                extraLinks = extraLinks,
                missingLinks = missingLinks.toSet(),
                patchMods = patchMods
            )
        }

        private val logger = Logger("PatchResolver")
    }
}
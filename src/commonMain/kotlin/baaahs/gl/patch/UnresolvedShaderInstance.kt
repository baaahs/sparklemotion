package baaahs.gl.patch

import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutableLink
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderChannel

data class UnresolvedShaderInstance(
    val mutableShader: MutableShader,
    val incomingLinksOptions: Map<String, MutableSet<MutableLink.Port>>,
    var shaderChannel: ShaderChannel? = null,
    var priority: Float
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
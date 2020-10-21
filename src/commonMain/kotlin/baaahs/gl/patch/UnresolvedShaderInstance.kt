package baaahs.gl.patch

import baaahs.gl.shader.InputPort
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderChannel
import baaahs.unknown

class UnresolvedShaderInstance(
    val mutableShader: MutableShader,
    val incomingLinksOptions: Map<InputPort, MutableSet<MutablePort>>,
    var shaderChannel: ShaderChannel = ShaderChannel.Main,
    var priority: Float
) {
    fun isAmbiguous() = incomingLinksOptions.values.any { it.size > 1 }

    fun describeAmbiguity(): String {
        return mutableShader.title + ": " +
                incomingLinksOptions
                    .filter { (_, links) -> links.size > 1 }
                    .map { (portId, links) ->
                        "$portId->(${links.joinToString(",") { it.title }}"
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

    fun linkOptionsFor(portId: String): MutableSet<MutablePort> {
        val key = incomingLinksOptions.keys.find { it.id == portId }
            ?: throw error(unknown("port", portId, incomingLinksOptions.keys.map { it.id }))
        return linkOptionsFor(key)
    }

    fun linkOptionsFor(inputPort: InputPort) =
        incomingLinksOptions.getValue(inputPort)

    override fun toString(): String {
        return "UnresolvedShaderInstance(shader=${mutableShader.title})"
    }

    fun takeFirstIfAmbiguous() {
        if (isAmbiguous()) {
            incomingLinksOptions.values.forEach { options ->
                if (options.size > 1) {
                    val first = options.first()
                    options.clear()
                    options.add(first)
                }
            }
        }
    }
}

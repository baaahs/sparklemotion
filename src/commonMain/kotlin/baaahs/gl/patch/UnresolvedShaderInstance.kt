package baaahs.gl.patch

import baaahs.show.ShaderChannel
import baaahs.show.ShaderChannelSourcePort
import baaahs.show.SourcePort
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderChannelSourcePort
import baaahs.show.mutable.MutableSourcePort

class UnresolvedShaderInstance(
    val mutableShader: MutableShader,
    val incomingLinksOptions: Map<String, MutableSet<SourcePortOption>>,
    var shaderChannel: ShaderChannel = ShaderChannel.Main,
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
            val shaderChannelOptions = options.filter {
                it is ResolvedSourcePortOption && it.sourcePort is MutableShaderChannelSourcePort
            }
            if (options.size > 1 && shaderChannelOptions.size == 1) {
                options.clear()
                options.add(shaderChannelOptions.first())
            }
        }
    }

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

interface SourcePortOption {
    val origin: Origin
    fun displayName(): String

    companion object {
        val defaultOrder = compareByDescending<SourcePortOption> { it.origin.priority }
    }

    enum class Origin(val priority: Float) {
        HUH(1f)
    }
}

class ResolvedSourcePortOption(val sourcePort: MutableSourcePort) : SourcePortOption {
    override val origin: SourcePortOption.Origin
        get() = SourcePortOption.Origin.HUH

    override fun displayName() = sourcePort.displayName()
}

class UnresolvedInstanceSourcePortOption(val unresolvedShaderInstance: UnresolvedShaderInstance) : SourcePortOption {
    override val origin: SourcePortOption.Origin
        get() = SourcePortOption.Origin.HUH

    override fun displayName() = unresolvedShaderInstance.mutableShader.title
}

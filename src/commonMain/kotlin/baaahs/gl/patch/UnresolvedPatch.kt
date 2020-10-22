package baaahs.gl.patch

import baaahs.show.Shader
import baaahs.show.Surfaces
import baaahs.show.mutable.*
import baaahs.unknown

class UnresolvedPatch(private val unresolvedShaderInstances: List<UnresolvedShaderInstance>) {
    fun editShader(shader: Shader): UnresolvedShaderInstance {
        return unresolvedShaderInstances.find { it.mutableShader.build() == shader }
            ?: error("Couldn't find shader \"${shader.title}\"")
    }

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
                it.incomingLinksOptions.entries.associate { (port, fromPortOptions) ->
                    port.id to
                            (fromPortOptions.firstOrNull()?.getMutablePort()
                                ?: MutableConstPort(port.type.defaultInitializer()))
                }.toMutableMap(),
                MutableShaderChannel(it.shaderChannel.id),
                it.priority
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
                        MutableShaderOutPort(fromShaderInstance)
                }
            }
        }

        return MutablePatch(shaderInstances.values.toList(), Surfaces.AllSurfaces)
    }

    fun acceptSuggestedLinkOptions(): UnresolvedPatch {
        unresolvedShaderInstances.forEach { it.takeFirstIfAmbiguous() }
        return this
    }
}

package baaahs.gl.patch

import baaahs.show.Shader
import baaahs.show.Surfaces
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.MutableShaderOutPort
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
                it.incomingLinksOptions.mapValues { (_, fromPortOptions) ->
                    fromPortOptions.first()
                }.toMutableMap(),
                it.shaderChannel,
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

    fun acceptSymbolicChannelLinks(): UnresolvedPatch {
        unresolvedShaderInstances.forEach { it.acceptSymbolicChannelLinks() }
        return this
    }

    fun takeFirstIfAmbiguous(): UnresolvedPatch {
        unresolvedShaderInstances.forEach { it.takeFirstIfAmbiguous() }
        return this
    }
}

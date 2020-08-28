package baaahs.gl.patch

import baaahs.getBang
import baaahs.show.Shader
import baaahs.show.ShaderOutSourcePort
import baaahs.show.Surfaces
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.MutableShaderOutSourcePort
import baaahs.show.mutable.MutableSourcePort

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
        val mutableShaderInstances = unresolvedShaderInstances.associate {
            it to MutableShaderInstance(it.mutableShader, hashMapOf(), it.shaderChannel, it.priority)
        }

        // Second pass: resolve references between shaders to the correct instance editor.
        mutableShaderInstances.forEach { (unresolved, mutable) ->
            unresolved.incomingLinksOptions.forEach { (toPortId, sourcePortOptions) ->
                val sourcePortOption = sourcePortOptions.sortedWith(SourcePortOption.defaultOrder).first()
                when (sourcePortOption) {
                    is UnresolvedInstanceSourcePortOption -> {
                        val otherShaderInstance = mutableShaderInstances.getBang(sourcePortOption.unresolvedShaderInstance, "shader instance")
                        mutable.incomingLinks[toPortId] = MutableShaderOutSourcePort(otherShaderInstance)
                    }
                    is ResolvedSourcePortOption -> {
                        mutable.incomingLinks[toPortId] = sourcePortOption.sourcePort
                    }
                }
            }
        }

        return MutablePatch(mutableShaderInstances.values.toList(), Surfaces.AllSurfaces)
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

package baaahs.gl.patch

import baaahs.show.Shader
import baaahs.show.Surfaces
import baaahs.show.mutable.MutableConstPort
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderChannel
import baaahs.show.mutable.MutableShaderInstance
import baaahs.util.Logger

class UnresolvedPatch(private val unresolvedShaderInstances: List<UnresolvedShaderInstance>) {
    fun editShader(shader: Shader): UnresolvedShaderInstance {
        // TODO: src == src is probably the wrong check here:
        return unresolvedShaderInstances.find { it.mutableShader.src == shader.src }
            ?: error("Couldn't find shader \"${shader.title}\"")
    }

    fun editAll(callback: UnresolvedShaderInstance.() -> Unit): UnresolvedPatch {
        unresolvedShaderInstances.forEach { it.callback() }
        return this
    }

    fun isAmbiguous() = unresolvedShaderInstances.any { it.isAmbiguous() }

    fun confirm(): MutablePatch {
        if (isAmbiguous()) {
            error("ambiguous! " +
                    unresolvedShaderInstances
                        .filter { it.isAmbiguous() }
                        .map { it.describeAmbiguity() }
            )
        }

        // Create a shader instance editor for each shader.
        val shaderInstances = unresolvedShaderInstances.associate {
            it.mutableShader.build() to MutableShaderInstance(
                it.mutableShader,
                it.incomingLinksOptions.entries.associate { (port, fromPortOptions) ->
                    port.id to
                            (fromPortOptions.firstOrNull()?.getMutablePort()
                                ?: MutableConstPort(port.type.defaultInitializer(), port.type))
                }.toMutableMap(),
                MutableShaderChannel(it.shaderChannel.id),
                it.priority
            )
        }

        return MutablePatch(shaderInstances.values.toList(), Surfaces.AllSurfaces)
    }

    fun dumpOptions(): UnresolvedPatch {
        logger.info { "Unresolved Patch:" }
        unresolvedShaderInstances.forEach { unresolvedShaderInstance ->
            logger.info { "* ${unresolvedShaderInstance.mutableShader.title}" }
            unresolvedShaderInstance.incomingLinksOptions.forEach { (inputPort, linkOptions) ->
                logger.info { "  ${inputPort.id} (${inputPort.contentType}) ->" }
                linkOptions.forEach { linkOption ->
                    logger.info { "    * ${linkOption.title}"}
                }
            }
        }
        return this
    }

    fun acceptSuggestedLinkOptions(): UnresolvedPatch {
        unresolvedShaderInstances.forEach { it.takeFirstIfAmbiguous() }
        return this
    }

    companion object {
        private val logger = Logger<UnresolvedPatch>()
    }
}
